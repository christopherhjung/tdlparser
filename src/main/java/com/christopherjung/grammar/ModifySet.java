package com.christopherjung.grammar;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ModifySet implements Iterable<Object>
{
    private Object[] tokens;

    public ModifySet(Object[] tokens)
    {
        this.tokens = tokens;
    }

    public int size()
    {
        return tokens.length;
    }

    public Object get(int pos)
    {
        return tokens[pos];
    }

    @Override
    public Iterator<Object> iterator()
    {
        return List.of(tokens).iterator();
    }

    @Override
    public String toString()
    {
        return Arrays.toString(tokens);
    }
}