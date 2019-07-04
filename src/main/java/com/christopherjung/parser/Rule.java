package com.christopherjung.parser;

import java.util.ArrayList;
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

        if(!name.equals(rule.name)){
            return false;
        }

        return Arrays.equals(keys, rule.keys);
    }


    @Override
    public String toString()
    {
        return name + "->" + (keys.length == 0 ? "''" : String.join(" ", keys));
    }
}
