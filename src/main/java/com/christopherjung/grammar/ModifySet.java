package com.christopherjung.grammar;

import java.util.Iterator;
import java.util.List;

public class ModifySet implements Iterable<Object>
{
    private List<Object> collection;

    public ModifySet(List<Object> collection)
    {
        this.collection = collection;
    }

    public int size()
    {
        return collection.size();
    }

    public Object get(int pos)
    {
        return collection.get(pos);
    }

    @Override
    public Iterator<Object> iterator()
    {
        return collection.iterator();
    }

    @Override
    public String toString()
    {
        return collection.toString();
    }
}