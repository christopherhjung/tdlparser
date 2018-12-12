package com.christopherjung.reflectparser;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.translator.ParserTable;
import com.christopherjung.translator.ParserTableGenerator;
import com.christopherjung.translator.Rule;
import com.christopherjung.translator.TDLParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectTLDGenerator
{
    private static final Pattern nullablePattern = Pattern.compile("(?:([^(\\s]+)|\\(([^()]+)\\))\\?");

    private HashMap<Rule, Modifier> modifiers;
    private Grammar.Builder builder;
    private HashMap<String, Set<Class<?>>> returnTypes;

    public TDLParser generate(Class<?> clazz)
    {
        modifiers = new HashMap<>();

        HashMap<String, Method> nodeMethods = getNodeMethods(clazz);

        Method rootMethod = getRootMethod(clazz);

        returnTypes = getReturnTypes(nodeMethods);

        builder = new Grammar.Builder();

        addRootModifier(rootMethod);
        addNodeModifiers(nodeMethods);

        Grammar grammar = builder.build();

        List<Field> structureFields = getAnnotatedFields(clazz, ParserIgnore.class);
        Set<String> parserIgnores = getIgnores(structureFields);

        ParserTable table = new ParserTableGenerator().generate(grammar, parserIgnores);

        System.out.println(table.getEntries().size());

        ModifierSource source = new ModifierSource(modifiers);

        return new TDLParser(table, source);
    }

    public Set<String> getIgnores(List<Field> ignoreFields)
    {
        Set<String> ignores = new HashSet<>();

        for (Field field : ignoreFields)
        {
            ignores.add(field.getName());
        }

        return ignores;
    }

    public <T extends Annotation> List<Field> getAnnotatedFields(Class<?> clazz, Class<T> anno)
    {
        List<Field> nodeMethods = new ArrayList<>();
        for (Field field : clazz.getFields())
        {
            T[] nodes = field.getAnnotationsByType(anno);

            if (nodes.length != 1 || !field.getType().equals(String.class))
            {
                continue;
            }

            nodeMethods.add(field);
        }
        return nodeMethods;
    }

    public void addRootModifier(Method rootMethod)
    {
        ParserRoot parserRoot = getRootRule(rootMethod);
        addModifier(builder::setRootRule, rootMethod, parserRoot.value());
    }

    public void addNodeModifiers(HashMap<String, Method> nodeMethods)
    {
        for (String node : nodeMethods.keySet())
        {
            Method method = nodeMethods.get(node);
            addModifier(builder::addRule, method, node);
        }
    }

    public void addModifier(Function<String, Rule> ruleFunction, Method method, String nodeValue)
    {
        Modifier modifier = createModifier(method, nodeValue);

        String rawRule = removeKeys(nodeValue);
        Rule rule = ruleFunction.apply(rawRule);
        modifiers.put(rule, modifier);
    }

    private ParserRoot getRootRule(Method method)
    {
        ParserRoot[] nodes = method.getAnnotationsByType(ParserRoot.class);

        if (nodes.length != 1 && nodes[0] != null)
        {
            throw new RuntimeException("No Root ParserRule found");
        }

        return nodes[0];
    }

    private Set<Class<?>> getTypes(String rule)
    {
        if (returnTypes.containsKey(rule))
        {
            return returnTypes.get(rule);
        }

        return Set.of(String.class);
    }

    private void checkMethodForModifier(Method method, int[] keyMap, int[][] mapping, String ruleSet)
    {
        String[] valueSet = getValueSet(ruleSet);

        Parameter[] parameter = method.getParameters();


        if (parameter.length < mapping.length || mapping.length > valueSet.length)
        {
            throw new RuntimeException("Wrong Parameter Size " + method.getName() + " with " + Arrays.toString(parameter) + " " + Arrays.deepToString(mapping) + " " + Arrays.toString(valueSet));
        }

        for (int i = keyMap.length - 1; i >= 0; i--)
        {
            int paramIndex = keyMap[i];

            if (paramIndex >= parameter.length)
            {
                throw new RuntimeException("Index " + paramIndex + " not inside parameter List");
            }

            Parameter param = parameter[paramIndex];

            for (int j = 0; j < mapping[i].length; j++)
            {
                Set<Class<?>> right = getTypes(valueSet[mapping[i][j]]);

                for (Class<?> returnType : right)
                {
                    if (!param.getType().isAssignableFrom(returnType))
                    {

                        throw new RuntimeException("In Method \"" + method.getName() + "\" Parameter \"" + param.getName() + "\" type " + param.getType().getSimpleName() + " not assignable from " + valueSet[i] + " " + right);
                    }
                }
            }
        }
    }

    private static class ReflectModifier implements Modifier
    {
        private Method method;
        private HashMap<Integer, Integer> mapping;
        private HashMap<Integer, Supplier<?>> defaultParameters;
        private Object[] values;

        public ReflectModifier(Method method, HashMap<Integer, Integer> mapping, HashMap<Integer, Supplier<?>> defaultParameters)
        {
            this.method = method;
            this.mapping = mapping;
            this.defaultParameters = defaultParameters;
            values = new Object[method.getParameterCount()];
        }

        @Override
        public Object modify()
        {
            for (int i : defaultParameters.keySet())
            {
                values[i] = defaultParameters.get(i).get();
            }

            try
            {
                return method.invoke(null, values);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
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

    private Modifier createModifier(Method method, String ruleSet)
    {
        String[] keySet = getKeySet(ruleSet);

        HashMap<String, Integer> ruleMapping = new HashMap<>();

        for (int i = 0; i < keySet.length; i++)
        {
            ruleMapping.putIfAbsent(keySet[i], i);
        }

        HashMap<Integer, Integer> mapping = getKeyMapping(method, ruleMapping);
        HashMap<Integer, Supplier<?>> defaultParameters = getDefaultConstructors(method, ruleMapping.keySet());

        return new ReflectModifier(method, mapping, defaultParameters);
    }

    public Method getRootMethod(Class<?> clazz)
    {
        Method rootMethod = null;
        for (Method method : clazz.getMethods())
        {
            ParserRoot[] parserRoots = method.getAnnotationsByType(ParserRoot.class);

            if (parserRoots.length == 1)
            {
                rootMethod = method;
                break;
            }
        }

        return rootMethod;
    }

    public HashMap<String, Method> getNodeMethods(Class<?> clazz)
    {
        HashMap<String, Method> nodeMethods = new HashMap<>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods)
        {
            for (ParserRule parserRule : method.getAnnotationsByType(ParserRule.class))
            {
                for (String perm : getRulePermutations(parserRule.value()))
                {
                    if (nodeMethods.containsKey(perm))
                    {
                        throw new RuntimeException("exist already " + nodeMethods);
                    }

                    nodeMethods.put(perm, method);
                }
            }
        }

        return nodeMethods;
    }

    public HashSet<String> getRulePermutations(String node)
    {
        HashSet<String> set = new HashSet<>();

        Matcher matcher = nullablePattern.matcher(node);

        if (matcher.find())
        {
            set.addAll(getRulePermutations(matcher.replaceFirst("$1$2")));
            set.addAll(getRulePermutations(matcher.replaceFirst("")));
        }
        else
        {
            set.add(node);
        }

        return set;
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


    public HashMap<Integer, Integer> getKeyMapping(Method method, HashMap<String, Integer> ruleMap)
    {
        HashMap<Integer, Integer> keyMap = new HashMap<>();

        int i = 0;
        for (Parameter parameter : method.getParameters())
        {
            if (ruleMap.containsKey(parameter.getName()))
            {
                keyMap.put(ruleMap.get(parameter.getName()), i);
            }

            i++;
        }

        return keyMap;
    }

    public HashMap<Integer, Supplier<?>> getDefaultConstructors(Method method, Set<String> usedParameters)
    {
        Parameter[] parameters = method.getParameters();
        HashMap<Integer, Supplier<?>> keyMap = new HashMap<>();

        int i = 0;
        for (Parameter parameter : method.getParameters())
        {
            if (!usedParameters.contains(parameter.getName()))
            {
                try
                {
                    Class<?> type = parameters[i].getType();

                    if (List.class.equals(type))
                    {
                        type = ArrayList.class;
                    }

                    Constructor<?> constructor = type.getConstructor();
                    keyMap.put(i, () -> {
                        try
                        {
                            return constructor.newInstance();
                        }
                        catch (Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                    });
                }
                catch (Exception e)
                {
                    keyMap.put(i, () -> null);
                }
            }

            i++;
        }

        return keyMap;
    }

    public String[] getKeySet(String ruleNames)
    {
        return getRecipeSet(ruleNames, 0);
    }

    public String[] getValueSet(String ruleNames)
    {
        return getRecipeSet(ruleNames, 1);
    }

    public String[] getRecipeSet(String ruleNames, int type)
    {
        String[] recipe = getRecipe(ruleNames);

        for (int i = 0; i < recipe.length; i++)
        {
            String entry = recipe[i];

            if (entry.length() > 1)
            {
                String[] keyValue = entry.split(":");

                if (keyValue.length > 1)
                {
                    recipe[i] = keyValue[type];
                }
            }
        }

        return recipe;
    }

    public String[] getRecipe(String ruleNames)
    {
        int pos = ruleNames.indexOf("->");
        if (pos > 0)
        {
            ruleNames = ruleNames.substring(pos + 2).trim();
        }

        return ruleNames.split("\\s+");
    }


    public HashMap<String, List<Integer>> getRoute(Set<String> parameters, String[] keySet)
    {
        HashMap<String, List<Integer>> route = new HashMap<>();

        for (int i = 0; i < keySet.length; i++)
        {
            if (parameters.contains(keySet[i]))
            {
                route.computeIfAbsent(keySet[i], (key) -> new ArrayList<>()).add(i);
            }
        }

        return route;
    }

    public String removeKeys(String rule)
    {
        return rule.replaceAll("\\w+:(\\S+)", "$1");
    }
}
