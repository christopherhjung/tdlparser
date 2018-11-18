package com.christopherjung.grammar;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class ValueNode<T> extends TreeNode<T>
{
    private T value;

    public ValueNode(T value)
    {
        this.value = value;
    }

    public ValueNode()
    {
        this(null);
    }

    public T getValue()
    {
        return value;
    }

    public boolean isEpsilon()
    {
        return value == null;
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        if (!isEpsilon())
        {
            sb.append(value);
        }
    }

    @Override
    public String toString()
    {
        return value + "";
    }

    public static <T> Collection<T> rawOf(T... chars)
    {
        HashSet<T> set = new HashSet<>();
        Collections.addAll(set, chars);
        return set;
    }

    @Override
    public ValueNode<T> clone()
    {
        return new ValueNode<>(value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof ValueNode)) return false;
        ValueNode other = (ValueNode) obj;


        return value == other.value;
    }
}