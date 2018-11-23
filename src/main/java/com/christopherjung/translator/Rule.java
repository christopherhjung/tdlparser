package com.christopherjung.translator;

import java.util.Arrays;

public class Rule
{
    private int id;
    private final String name;
    private final String[] keys;

    public Rule(int id, String name, String[] keys)
    {
        this.id = id;
        this.name = name;
        this.keys = keys;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getKey(int index)
    {
        return keys[index];
    }

    public int size()
    {
        return keys.length;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof Rule)) return false;
        Rule rule = (Rule) obj;

        return name.equals(rule.name) && Arrays.equals(keys, rule.keys);
    }


    @Override
    public String toString()
    {
        return name + "->" + String.join(" ", keys);
    }
}
