package com.christopherjung.reflectparser;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.translator.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

public class ReflectTLDGenerator
{

    private HashMap<Rule, Modifier> modifiers;
    private Grammar.Builder builder;
    private HashMap<String, Class<?>> returnTypes;

    public TDLParser generate(Class<?> clazz)
    {
        modifiers = new HashMap<>();

        HashMap<Node, Method> nodeMethods = getNodeMethods(clazz);

        Method rootMethod = getRootMethod(clazz);

        returnTypes = getReturnTypes(nodeMethods);

        builder = new Grammar.Builder();

        addRootModifier(rootMethod);
        addNodeModifiers(nodeMethods);

        Grammar grammar = builder.build();

        List<Field> structureFields = getAnnotatedFields(clazz, ParserIgnore.class);
        Set<String> parserIgnores = getIgnores(structureFields);

        ClosureTable closureTable = new ClosureTable(grammar, parserIgnores);
        ParserTable table = closureTable.getTable();

        System.out.println(TDLUtils.toString(table));

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
        RootNode rootNode = getRootRule(rootMethod);
        addModifier(builder::setRootRule, rootMethod, rootNode.value());
    }

    public void addNodeModifiers(HashMap<Node, Method> nodeMethods)
    {
        for (Node node : nodeMethods.keySet())
        {
            Method method = nodeMethods.get(node);
            addModifier(builder::addRule, method, node.value());
        }
    }

    public void addModifier(Function<String, Rule> ruleFunction, Method method, String nodeValue)
    {
        String rawRule = removeKeys(nodeValue);
        Modifier modifier = createModifier(method, nodeValue);
        Rule rule = ruleFunction.apply(rawRule);
        modifiers.put(rule, modifier);
    }

    private RootNode getRootRule(Method method)
    {
        RootNode[] nodes = method.getAnnotationsByType(RootNode.class);

        if (nodes.length != 1 && nodes[0] != null)
        {
            throw new RuntimeException("No Root Node found");
        }

        return nodes[0];
    }

    private Class<?> getType(String rule)
    {
        if (returnTypes.containsKey(rule))
        {
            return returnTypes.get(rule);
        }

        return String.class;
    }

    private void checkMethodForModifier(Method method, int[][] mapping, String ruleSet)
    {
        String[] valueSet = getValueSet(ruleSet);

        Parameter[] parameter = method.getParameters();

        if (parameter.length != mapping.length || mapping.length > valueSet.length)
        {
            throw new RuntimeException("Wrong Parameter Size " + method.getName());
        }

        for (int i = parameter.length - 1; i >= 0; i--)
        {
            Parameter param = parameter[i];

            for (int j = 0; j < mapping[i].length; j++)
            {
                Class<?> right = getType(valueSet[mapping[i][j]]);

                if (!param.getType().isAssignableFrom(right))
                {
                    throw new RuntimeException("In Method \"" + method.getName() + "\" Parameter \"" + param.getName() + "\" type " + param.getType().getSimpleName() + " not assignable from " + returnTypes.get(valueSet[i]).getSimpleName());
                }
            }
        }
    }

    private int[][] getEqualsCheck()
    {
        return null;
    }

    private Modifier createModifier(Method method, String ruleSet)
    {
        String[] keySet = getKeySet(ruleSet);

        HashMap<String, List<Integer>> route = getRoute(keySet);
        int[][] mapping = getMapping(method, route);

        checkMethodForModifier(method, mapping, ruleSet);

        return set -> {

            Object[] objs = new Object[method.getParameterCount()];

            for (int i = 0; i < objs.length; i++)
            {
                objs[i] = set.get(mapping[i][0]);

                //equals check if more than one common key
                for (int j = 1; j < mapping[i].length; j++)
                {
                    if (!objs[i].equals(set.get(mapping[i][j])))
                    {
                        throw new RuntimeException("Not equals " + objs[i] + " " + set.get(mapping[i][j]));
                    }
                }
            }

            try
            {
                return method.invoke(null, objs);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        };
    }

    public Class<?> findClosestCommonSuper(Class<?> a, Class<?> b)
    {
        if (b.isAssignableFrom(a))
        {
            return b;
        }

        while (!a.isAssignableFrom(b))
        {
            a = a.getSuperclass();
        }

        return a;
    }

    public Method getRootMethod(Class<?> clazz)
    {
        Method rootMethod = null;
        for (Method method : clazz.getMethods())
        {
            RootNode[] rootNodes = method.getAnnotationsByType(RootNode.class);

            if (rootNodes.length == 1)
            {
                rootMethod = method;
                break;
            }
        }

        return rootMethod;
    }

    public HashMap<Node, Method> getNodeMethods(Class<?> clazz)
    {
        HashMap<Node, Method> nodeMethods = new HashMap<>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods)
        {
            for (Node node : method.getAnnotationsByType(Node.class))
            {
                nodeMethods.put(node, method);
            }
        }
        return nodeMethods;
    }

    public HashMap<String, Class<?>> getReturnTypes(Map<Node, Method> nodeMethods)
    {
        HashMap<String, Class<?>> returnTypes = new HashMap<>();

        for (var entry : nodeMethods.entrySet())
        {
            Method method = entry.getValue();
            Node node = entry.getKey();

            String value = node.value();

            String key = value.substring(0, value.indexOf("->")).trim();

            Class<?> returnClass = method.getReturnType();

            if (returnTypes.containsKey(key))
            {
                returnClass = findClosestCommonSuper(returnTypes.get(key), returnClass);
            }

            returnTypes.put(key, returnClass);
        }

        return returnTypes;
    }

    public int[][] getMapping(Method method, HashMap<String, List<Integer>> route)
    {
        int[][] mapping = new int[method.getParameterCount()][];

        int i = 0;
        for (Parameter param : method.getParameters())
        {
            if (!route.containsKey(param.getName()))
            {
                throw new RuntimeException("no " + param.getName());
            }

            List<Integer> routingList = route.get(param.getName());
            mapping[i] = new int[routingList.size()];

            int j = 0;
            for (int index : routingList)
            {
                mapping[i][j++] = index;
            }

            i++;
        }

        return mapping;
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

    public HashMap<String, List<Integer>> getRoute(String ruleNames)
    {
        return getRoute(getKeySet(ruleNames));
    }

    public HashMap<String, List<Integer>> getRoute(String[] keySet)
    {
        HashMap<String, List<Integer>> route = new HashMap<>();

        for (int i = 0; i < keySet.length; i++)
        {
            route.computeIfAbsent(keySet[i], (key) -> new ArrayList<>()).add(i);
        }

        return route;
    }

    public String removeKeys(String rule)
    {
        return rule.replaceAll("\\w+:(\\S+)", "$1");
    }
}
