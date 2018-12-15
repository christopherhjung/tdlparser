package com.christopherjung.reflectparser;

import com.christopherjung.container.*;
import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.translator.ParserTable;
import com.christopherjung.translator.ParserTableGenerator;
import com.christopherjung.translator.Rule;
import com.christopherjung.translator.TDLParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectTLDGenerator
{
    private HashMap<Rule, Modifier> modifiers;
    private Grammar.Builder builder;
    private HashMap<String, Set<Class<?>>> returnTypes;

    public TDLParser generate(Class<?> clazz)
    {
        modifiers = new HashMap<>();

        HashMap<String, Method> nodeMethods = getNodeMethods(clazz);

        returnTypes = getReturnTypes(nodeMethods);

        builder = new Grammar.Builder();

        addModifiers(nodeMethods);

        Grammar grammar = builder.build();

        List<Field> structureFields = getAnnotatedFields(clazz, ParserIgnore.class);
        Set<String> parserIgnores = structureFields.stream().map(Field::getName).collect(Collectors.toSet());

        ParserTable table = new ParserTableGenerator().generate(grammar, parserIgnores);

        System.out.println(table.getEntries().size());

        ModifierSource source = new ModifierSource(modifiers);

        return new TDLParser(table, source);
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

        Mapper mapper = new Mapper(method.getParameterCount())
        {
            @Override
            Object map(Object[] values)
            {
                try
                {
                    return method.invoke(null, values);
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
        List<Permutation> currentRule = generatePermutations(ruleTree);

        for (Permutation permutation : currentRule)
        {
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

            Modifier modifier = new ReflectModifier(mapper, mapping);
            modifiers.put(rule, modifier);
        }
    }

    private static class Permutation implements Cloneable
    {
        HashMap<Integer, String> mapping;
        List<String> rule;

        public Permutation()
        {
            this(new HashMap<>(), new ArrayList<>());
        }

        public Permutation(HashMap<Integer, String> mapping, List<String> rule)
        {
            this.mapping = mapping;
            this.rule = rule;
        }

        private void addValue(String value)
        {
            mapping.put(rule.size(), value);
            rule.add(value);
        }

        private void addPermutation(Permutation other)
        {
            for (int i : other.mapping.keySet())
            {
                mapping.put(i + rule.size(), other.mapping.get(i));
            }
            rule.addAll(other.rule);
        }

        private void rename(String name)
        {
            for (int i : mapping.keySet())
            {
                mapping.put(i, name);
            }
        }

        @Override
        protected Permutation clone()
        {
            return new Permutation(new HashMap<>(mapping), new ArrayList<>(rule));
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
                String name = "list#" + generateName(subTree);

                List<Permutation> permutations = new ArrayList<>();
                Permutation list = new Permutation();
                list.addValue(name);
                permutations.add(list);

                if (node instanceof StarNode)
                {
                    permutations.add(new Permutation());
                }

                HashMap<String, Integer> nameMappingFirst = new HashMap<>();
                nameMappingFirst.put(name, 0);
                nameMappingFirst.put("element", 1);
                addModifier(name, new Mapper(2)
                {
                    @Override
                    Object map(Object[] values)
                    {
                        List list = (List) values[0];
                        list.add(values[1]);
                        return list;
                    }
                }, nameMappingFirst, new ConcatNode<>(new ValueNode<>(name), new NameNode<>("element", subTree)));


                HashMap<String, Integer> nameMappingSecond = new HashMap<>();
                nameMappingSecond.put("element", 0);
                addModifier(name, new Mapper(1)
                {
                    @Override
                    Object map(Object[] values)
                    {
                        List list = new ArrayList<>();
                        list.add(values[0]);
                        return list;
                    }
                }, nameMappingSecond, new NameNode<>("element", subTree));

                return permutations;
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

            Permutation value = new Permutation();
            value.addValue(valueNode.getValue());
            return new ArrayList<>(List.of(value));
        }
        else if (node instanceof OptionNode)
        {
            OptionNode<String> optionNode = (OptionNode<String>) node;
            TreeNode<String> subTree = optionNode.getTarget();
            String name = "sequence" + optionCounter++ + "#" + generateName(subTree);
            List<Permutation> permutations = new ArrayList<>();
            Permutation list = new Permutation();
            list.addValue(name);
            permutations.add(list);


            if (optionNode.getOption("min") == null || optionNode.getOption("min").equals("0"))
            {
                permutations.add(new Permutation());
            }

            TreeNode<String> listNode;
            if (optionNode.getOption("separator") != null)
            {
                listNode = new ConcatNode<>(new ConcatNode<>(new ValueNode<>(name), new ValueNode<>(optionNode.getOption("separator"))), new NameNode<>("element", subTree));
            }
            else
            {
                listNode = new ConcatNode<>(new ValueNode<>(name), new NameNode<>("element", subTree));
            }

            HashMap<String, Integer> nameMapper = new HashMap<>();
            nameMapper.put(name, 0);
            nameMapper.put("element", 1);
            addModifier(name, new Mapper(2)
            {
                @Override
                Object map(Object[] values)
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
                Object map(Object[] values)
                {
                    List<Object> list = new ArrayList<>();
                    list.add(values[0]);
                    return list;
                }
            }, nameMapperRight, new NameNode<>("element", subTree));

            return permutations;
        }

        throw new RuntimeException("frlfdhueiowpso " + node);
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

        abstract Object map(Object[] values);
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
        public Object modify()
        {
            return mapper.map(values);
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

        for (var entry : nodeMethods.entrySet())
        {
            Method method = entry.getValue();
            String value = entry.getKey();

            String key = value.substring(0, value.indexOf("->")).trim();

            returnTypes.computeIfAbsent(key, ($) -> new HashSet<>()).add(method.getReturnType());
        }

        return returnTypes;
    }
}
