package com.christopherjung.utils;

import java.util.HashMap;

public class Logger
{
    private static HashMap<Object, Long> counter = new HashMap<>();

    public static void count(Object str)
    {
        str = str.toString();
        counter.put(str, counter.getOrDefault(str, 0L) + 1L);
    }

    public static void printOverview()
    {
        for (Object key : counter.keySet())
        {
            String preview = key.toString();
            System.out.println(counter.get(key) + " times : " + preview );
        }
    }
}
