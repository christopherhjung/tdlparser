package com.christopherjung.reflectparser;

import com.christopherjung.container.*;
import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.parser.Generator;
import com.christopherjung.parser.Parser;
import com.christopherjung.parser.ParserTable;
import com.christopherjung.parser.Rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ReflectTLDGenerator
{
    private HashMap<Rule, Modifier> modifiers;
    private Grammar.Builder builder;
    //private HashMap<String, Set<Class<?>>> returnTypes;

    public static Parser generate(Class<?> clazz)
    {
        return new ReflectTLDGenerator().generateImpl(clazz);
    }

    private Parser generateImpl(Class<?> clazz)
    {
        modifiers = new HashMap<>();

        HashMap<String, Method> nodeMethods = getNodeMethods(clazz);

        //returnTypes = getReturnTypes(nodeMethods);

        builder = new Grammar.Builder();

        addModifiers(nodeMethods);

        Grammar grammar = builder.build();

        List<Field> structureFields = getAnnotatedFields(clazz, ParserIgnore.class);
        Set<String> parserIgnores = structureFields.stream().map(Field::getName).collect(Collectors.toSet());

        ParserTable table = new Generator().generate(grammar, parserIgnores);

        System.out.println(table.getEntries().size());

        Supplier<Object> parserSupplier;

        try
        {
            Constructor<?> constructor = clazz.getConstructor();
            parserSupplier = () -> {
                try
                {
                    return constructor.newInstance();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Invoke exception");
                }
            };
        }
        catch (Exception e)
        {
            throw new RuntimeException("No Default Constructor");
        }

        ModifierSource source = new ModifierSource(modifiers, parserSupplier);

        return new Parser(table, source);
    }

    public <T extends Annotation> List<Field> getAnnotatedFields(Class<?> clazz, Class<T> anno)
    {
        List<Field> nodeMethods = new ArrayList<>();
        for (Field field : clazz.getFields())
        {
            T[] nodes = field.getAnnotationsByType(anno);

            if (nodes.length == 0 || !field.getType().equals(String.class))
            {
                continue;
            }

            nodeMethods.add(field);
        }
        return nodeMethods;
    }

    public void addModifiers(HashMap<String, Method> nodeMethods)
    {
        for (String node : nodeMethods.keySet())
        {
            Method method = nodeMethods.get(node);
            addModifier(method, node);
        }
    }

    private void addModifier(Method method, String ruleSet)
    {
        String name = ruleSet.replaceFirst("->.+", "").trim();
        String components = ruleSet.replaceFirst(".*?->", "").trim();

        if (name.equals("root"))
        {
            components += " EOF";
        }
        TreeNode<String> tree = new RuleParser().parse(components);

        System.out.println(tree.toRegEx());

        Mapper mapper = new Mapper(method.getParameterCount())
        {
            @Override
            Object map(Object tag, Object[] values)
            {
                try
                {
                    return method.invoke(tag, values);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        };

        HashMap<String, Integer> nameMapping = new HashMap<>();

        int i = 0;
        for (Parameter parameter : method.getParameters())
        {
            nameMapping.put(parameter.getName(), i++);
        }

        addModifier(name, mapper, nameMapping, tree);
    }

    private void addModifier(String name, Mapper mapper, HashMap<String, Integer> nameMapping, TreeNode<String> ruleTree)
    {
        List<Permutation> permutations = generatePermutations(ruleTree);

        for (Permutation permutation : permutations)
        {
            if (permutation.size() == 0)
            {
                continue;
            }

            Mapper currentMapper = mapper;

            Rule rule = builder.addRule(name, permutation.rule.toArray(new String[0]));
            HashMap<Integer, Integer> mapping = new HashMap<>();

            for (int i : permutation.mapping.keySet())
            {
                Integer target = nameMapping.get(permutation.mapping.get(i));
                if (target != null)
                {
                    mapping.put(i, target);
                }
            }


            if (permutation.defaultValues.size() > 0)
            {

                HashMap<Integer, Supplier<?>> defaultValues = new HashMap<>();

                for (String key : permutation.defaultValues.keySet())
                {
                    defaultValues.put(nameMapping.get(key), permutation.defaultValues.get(key));
                }

                currentMapper = new Mapper(mapper.size)
                {
                    @Override
                    Object map(Object parser, Object[] values)
                    {
                        for (int key : defaultValues.keySet())
                        {
                            values[key] = defaultValues.get(key).get();
                        }

                        return mapper.map(parser, values);
                    }
                };
            }

            modifiers.put(rule, new ReflectModifier(currentMapper, mapping));
        }

    }

    private static class Permutation implements Cloneable
    {
        HashMap<Integer, String> mapping;
        HashMap<String, Supplier<?>> defaultValues;
        List<String> rule;

        public Permutation()
        {
            this(new HashMap<>(), new ArrayList<>(), new HashMap<>());
        }

        public Permutation(HashMap<Integer, String> mapping, List<String> rule, HashMap<String, Supplier<?>> defaultValues)
        {
            this.defaultValues = defaultValues;
            this.mapping = mapping;
            this.rule = rule;
        }

        private void addValue(String value)
        {
            mapping.put(rule.size(), value);
            rule.add(value);
        }

        public int size()
        {
            return rule.size();
        }

        private void addDefaultValue(String name, Supplier<?> supplier)
        {
            defaultValues.put(name, supplier);
        }

        private void addPermutation(Permutation other)
        {
            for (int i : other.mapping.keySet())
            {
                mapping.put(i + rule.size(), other.mapping.get(i));
            }
            rule.addAll(other.rule);
            defaultValues.putAll(other.defaultValues);
        }

        private void rename(String name)
        {
            for (int i : mapping.keySet())
            {
                mapping.put(i, name);
            }

            if (defaultValues.size() > 1)
            {
                throw new RuntimeException("not possible");
            }

            HashMap<String, Supplier<?>> renamed = new HashMap<>();

            for (Supplier<?> supplier : defaultValues.values())
            {
                renamed.put(name, supplier);
            }

            defaultValues = renamed;
        }

        @Override
        protected Permutation clone()
        {
            return new Permutation(new HashMap<>(mapping), new ArrayList<>(rule), new HashMap<>(defaultValues));
        }

        @Override
        public String toString()
        {
            return "{" + rule.toString() + " = " + mapping + "}";
        }
    }

    private int optionCounter = 0;

    private List<Permutation> generatePermutations(TreeNode<String> node)
    {
        if (node instanceof BinaryNode)
        {
            BinaryNode<String> binaryNode = (BinaryNode<String>) node;

            if (node instanceof OrNode)
            {
                List<Permutation> permutations = new ArrayList<>();

                permutations.addAll(generatePermutations(binaryNode.getLeft()));
                permutations.addAll(generatePermutations(binaryNode.getRight()));

                return permutations;
            }
            else if (node instanceof ConcatNode)
            {
                List<Permutation> permutations = new ArrayList<>();

                List<Permutation> left = generatePermutations(binaryNode.getLeft());
                List<Permutation> right = generatePermutations(binaryNode.getRight());

                for (int i = 0; i < left.size(); i++)
                {
                    for (int j = 0; j < right.size(); j++)
                    {
                        Permutation permutation = left.get(i).clone();
                        permutation.addPermutation(right.get(j));
                        permutations.add(permutation);
                    }
                }

                return permutations;
            }
        }
        else if (node instanceof UnaryNode)
        {
            UnaryNode<String> unaryNode = (UnaryNode<String>) node;

            if (node instanceof QuestNode)
            {
                List<Permutation> permutations = generatePermutations(unaryNode.getValue());
                permutations.add(new Permutation());

                return permutations;
            }
            else if (node instanceof StarNode || node instanceof PlusNode)
            {
                TreeNode<String> subTree = unaryNode.getValue();
                return sequence(subTree, node instanceof StarNode, null);
            }
            else if (node instanceof NameNode)
            {
                NameNode<String> nameNode = (NameNode<String>) node;
                List<Permutation> permutations = generatePermutations(unaryNode.getValue());
                String name = nameNode.getName();

                for (int i = 0; i < permutations.size(); i++)
                {
                    permutations.get(i).rename(name);
                }

                return permutations;
            }
        }
        else if (node instanceof ValueNode)
        {
            ValueNode<String> valueNode = (ValueNode<String>) node;

            String value = valueNode.getValue();

            List<Permutation> permutations = new ArrayList<>();

            Permutation permutation = new Permutation();
            permutation.addValue(value);
            permutations.add(permutation);

            return permutations;
        }
        else if (node instanceof SeperatorNode)
        {
            SeperatorNode<String> separatorNode = (SeperatorNode<String>) node;

            TreeNode<String> subTree = separatorNode.getTarget();
            List<Permutation> permutations = sequence(subTree,
                    false, separatorNode.getSeparator());

            return permutations;
        }

        throw new RuntimeException("Unknown TreeNode " + node);
    }


    public List<Permutation> sequence(TreeNode<String> subTree, boolean nullable, String separator)
    {
        String name;

        if (separator == null)
        {
            if (nullable)
            {
                name = "nullableList#" + generateName(subTree);
            }
            else
            {
                name = "list#" + generateName(subTree);
            }
        }
        else
        {
            name = "sequence" + optionCounter++ + "#" + generateName(subTree);
        }


        List<Permutation> permutations = new ArrayList<>();
        Permutation list = new Permutation();
        list.addValue(name);
        permutations.add(list);

        if (nullable)
        {
            Permutation permutation = new Permutation();
            permutation.addDefaultValue(  name, ArrayList::new);
            permutations.add(permutation);
        }

        TreeNode<String> listNode = new ValueNode<>(name);
        if (separator != null)
        {
            listNode = new ConcatNode<>(listNode, new ValueNode<>(separator));
        }

        listNode = new ConcatNode<>(listNode, new NameNode<>("element", subTree));

        HashMap<String, Integer> nameMapper = new HashMap<>();
        nameMapper.put(name, 0);
        nameMapper.put("element", 1);
        addModifier(name, new Mapper(2)
        {
            @Override
            Object map(Object tag, Object[] values)
            {
                List<Object> list = (List<Object>) values[0];
                list.add(values[1]);
                return list;
            }
        }, nameMapper, listNode);


        HashMap<String, Integer> nameMapperRight = new HashMap<>();
        nameMapperRight.put("element", 0);
        addModifier(name, new Mapper(1)
        {
            @Override
            Object map(Object tag, Object[] values)
            {
                List<Object> list = new ArrayList<>();
                list.add(values[0]);
                return list;
            }
        }, nameMapperRight, new NameNode<>("element", subTree));

        return permutations;
    }


    private String generateName(TreeNode<String> node)
    {
        String name;

        if (node instanceof ValueNode)
        {
            ValueNode<String> valueNode = (ValueNode<String>) node;

            name = valueNode.getValue();
        }
        else if (node instanceof OrNode)
        {
            OrNode<String> orNode = (OrNode<String>) node;

            name = generateName(orNode.getLeft()) + "or" + generateName(orNode.getRight());
        }
        else throw new RuntimeException("Not possible");

        return name;
    }

    public static abstract class Mapper
    {
        int size;

        public Mapper(int size)
        {
            this.size = size;
        }

        public int getSize()
        {
            return size;
        }

        abstract Object map(Object tag, Object[] values);
    }

    private static class ReflectModifier implements Modifier
    {
        private Mapper mapper;
        private HashMap<Integer, Integer> mapping;
        private Object[] values;

        public ReflectModifier(Mapper mapper, HashMap<Integer, Integer> mapping)
        {
            this.mapper = mapper;
            this.mapping = mapping;
            values = new Object[mapper.getSize()];
        }

        @Override
        public Object modify(Object parser)
        {
            return mapper.map(parser, values);
        }

        @Override
        public void register(int index, Object obj)
        {
            Integer objIndex = mapping.get(index);

            if (objIndex == null)
            {
                return;
            }

            values[objIndex] = obj;
        }
    }

    public HashMap<String, Method> getNodeMethods(Class<?> clazz)
    {
        HashMap<String, Method> nodeMethods = new HashMap<>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods)
        {
            for (ParserRule parserRule : method.getAnnotationsByType(ParserRule.class))
            {
                nodeMethods.put(parserRule.value(), method);
            }
        }

        return nodeMethods;
    }

    public HashMap<String, Set<Class<?>>> getReturnTypes(Map<String, Method> nodeMethods)
    {
        HashMap<String, Set<Class<?>>> returnTypes = new HashMap<>();

        for (Map.Entry<String,Method> entry : nodeMethods.entrySet())
        {
            Method method = entry.getValue();
            String value = entry.getKey();

            String key = value.substring(0, value.indexOf("->")).trim();

            returnTypes.computeIfAbsent(key, ($) -> new HashSet<>()).add(method.getReturnType());
        }

        return returnTypes;
    }
}
