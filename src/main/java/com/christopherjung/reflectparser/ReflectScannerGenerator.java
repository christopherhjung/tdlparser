package com.christopherjung.reflectparser;

import com.christopherjung.scanner.Scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class ReflectScannerGenerator
{
    public static Scanner generate(Class<?> clazz)
    {
        List<Field> structureFields = getAnnotatedFields(clazz, ScannerStructure.class);
        List<Field> ignoreFields = getAnnotatedFields(clazz, ScannerIgnore.class);
        List<Field> tokenFields = getAnnotatedFields(clazz, ScannerToken.class);

        var tokens = getTokens(tokenFields);
        String structure = getStructure(structureFields);
        String ignore = getIgnore(ignoreFields);

        Scanner.Builder builder = new Scanner.Builder();
        if (ignore != null)
        {
            builder.add("ignore", ignore);
        }

        builder.addStructureChars(structure);
        for (String name : tokens.keySet())
        {
            builder.add(name, tokens.get(name));
        }


        return builder.build();
    }

    public static String getIgnore(List<Field> ignores)
    {
        if (ignores.size() == 0)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        try
        {
            for (Field field : ignores)
            {
                if (sb.length() > 0)
                {
                    sb.append('|');
                }

                sb.append(field.get(null).toString());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Field");
        }

        return sb.toString();
    }

    public static String getStructure(List<Field> structures)
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            for (Field field : structures)
            {
                sb.append(field.get(null).toString());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Field");
        }

        return sb.toString();
    }

    public static HashMap<String, String> getTokens(List<Field> tokenFields)
    {
        HashMap<String, String> result = new LinkedHashMap<>();

        try
        {
            for (Field field : tokenFields)
            {
                ScannerToken token = field.getAnnotation(ScannerToken.class);

                String name = token.value();

                if (name.isEmpty())
                {
                    name = field.getName();
                }

                result.put(name, field.get(null).toString());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Field");
        }

        return result;
    }

    public static <T extends Annotation> List<Field> getAnnotatedFields(Class<?> clazz, Class<T> anno)
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

}
