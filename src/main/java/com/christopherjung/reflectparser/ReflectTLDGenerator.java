package com.christopherjung.reflectparser;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.translator.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ReflectTLDGenerator
{

    public static TDLParser generate(Class<?> clazz) throws Exception
    {
        HashMap<Rule, Modifier> modifiers = new HashMap<>();

        HashMap<Node, Method> nodeMethods = getNodeMethods(clazz);

        System.out.println(nodeMethods);
        Method rootMethod = getRootMethod(clazz);

        HashMap<String, Class<?>> returnTypes = getReturnTypes(nodeMethods);

        Grammar.Builder builder = new Grammar.Builder();

        addRootModifier(builder, modifiers, rootMethod);
        addNodeModifiers(builder, modifiers, nodeMethods);

        Grammar grammar = builder.build();

        ClosureTable closureTable = new ClosureTable(grammar);
        ParserTable table = closureTable.getTable();

        System.out.println(TDLUtils.toString(table));

        ModifierSource source = new ModifierSource(modifiers);

        return new TDLParser(table, source);
    }

    public static void addRootModifier(Grammar.Builder builder, HashMap<Rule, Modifier> modifiers, Method rootMethod)
    {
        RootNode rootNode = getRootRule(rootMethod);
        addModifier(builder::setRootRule, rootMethod, rootNode.value(), modifiers);
    }

    public static void addNodeModifiers(Grammar.Builder builder, HashMap<Rule, Modifier> modifiers, HashMap<Node, Method> nodeMethods)
    {
        for (Node node : nodeMethods.keySet())
        {
            Method method = nodeMethods.get(node);
            addModifier(builder::addRule, method, node.value(), modifiers);
        }
    }

    public static void addModifier(Function<String, Rule> ruleFunction, Method method, String nodeValue, HashMap<Rule, Modifier> modifiers)
    {
        HashMap<String, Integer> route = getRoute(nodeValue);
        int[] mapping = getMapping(method, route);
        String rawRule = removeKeys(nodeValue);
        Modifier modifier = createModifier(method, mapping);
        Rule rule = ruleFunction.apply(rawRule);
        modifiers.put(rule, modifier);
    }

    private static RootNode getRootRule(Method method)
    {
        RootNode[] nodes = method.getAnnotationsByType(RootNode.class);

        if (nodes.length != 1 && nodes[0] != null)
        {
            throw new RuntimeException("No Root Node found");
        }

        return nodes[0];
    }

    private static Modifier createModifier(Method method, int[] mapping)
    {
        return set -> {

            Object[] objs = new Object[method.getParameterCount()];

            for (int i = 0; i < objs.length; i++)
            {
                objs[i] = set.get(mapping[i]);
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

    public static Class<?> findClosestCommonSuper(Class<?> a, Class<?> b)
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

    public static Method getRootMethod(Class<?> clazz)
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

    public static HashMap<Node, Method> getNodeMethods(Class<?> clazz)
    {
        HashMap<Node, Method> nodeMethods = new HashMap<>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods)
        {
            Node[] nodes = method.getAnnotationsByType(Node.class);

            for (Node node : nodes)
            {
                nodeMethods.put(node, method);
            }
        }
        return nodeMethods;
    }

    public static HashMap<String, Class<?>> getReturnTypes(Map<Node, Method> nodeMethods)
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

    public static int[] getMapping(Method method, HashMap<String, Integer> route)
    {
        int[] mapping = new int[method.getParameterCount()];

        int i = 0;
        for (Parameter param : method.getParameters())
        {
            try
            {

                mapping[i++] = route.get(param.getName());
            }
            catch (RuntimeException e)
            {
                System.out.println(param.getName());
                throw e;
            }
        }

        return mapping;
    }

    public static HashMap<String, Integer> getRoute(String ruleNames)
    {
        int pos = ruleNames.indexOf("->");
        if (pos > 0)
        {
            ruleNames = ruleNames.substring(pos + 2).trim();
        }

        String[] values = ruleNames.split("\\s+");

        return getRoute(values);
    }

    public static HashMap<String, Integer> getRoute(String[] values)
    {
        HashMap<String, Integer> route = new HashMap<>();

        for (int i = 0; i < values.length; i++)
        {
            String entry = values[i];

            if (entry.length() > 1)
            {
                String[] keyValue = entry.split(":");

                if (keyValue.length == 1)
                {
                    route.put(entry, i);
                }
                else
                {
                    route.put(keyValue[0], i);
                    values[i] = keyValue[1];
                }
            }
        }

        return route;
    }

    public static String removeKeys(String rule)
    {
        return rule.replaceAll("\\w+:(\\w+)", "$1");
    }
}
