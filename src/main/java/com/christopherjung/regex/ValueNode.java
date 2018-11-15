package com.christopherjung.regex;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class ValueNode<T> extends TreeNode<T>
{
    private T value;

    public ValueNode(T value)
    {
        this.value = value;
        setNullable(false);

        addFirstPosition(this);
        addLastPosition(this);
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

    public static Collection<TreeNode<Character>> fromTo(int from, int to)
    {
        HashSet<TreeNode<Character>> set = new HashSet<>();
        fromTo(set, from, to);
        return set;
    }

    public static Collection<Character> rawFromTo(int from, int to)
    {
        HashSet<Character> set = new HashSet<>();
        for (int i = 0; i < to - from - 1; i++)
        {
            set.add((char) (from + i));
        }
        return set;
    }

    public static Collection<TreeNode> map(char... chars)
    {
        HashSet<TreeNode> result = new HashSet<>();
        for (int i = 0; i < chars.length; i++)
        {
            result.add(new ValueNode<>(chars[i]));
        }

        return result;
    }


    public static void fromTo(Collection<TreeNode<Character>> collection, int from, int to)
    {
        for (int i = 0; i < to - from - 1; i++)
        {
            collection.add(new ValueNode<>((char) (from + i)));
        }
    }

    public static <T> void map(Collection<TreeNode<T>> collection, T... chars)
    {
        for (int i = 0; i < chars.length; i++)
        {
            collection.add(new ValueNode<>(chars[i]));
        }
    }

    public static <T> Collection<TreeNode<T>> of(T... chars)
    {
        HashSet<TreeNode<T>> set = new HashSet<>();
        for (int i = 0; i < chars.length; i++)
        {
            set.add(new ValueNode<>(chars[i]));
        }
        return set;
    }

    public static <T> Collection<T> rawOf(T... chars)
    {
        HashSet<T> set = new HashSet<>();
        Collections.addAll(set, chars);
        return set;
    }

    public static void fromToChars(Collection<Character> collection, int from, int to)
    {
        for (int i = 0; i < to - from - 1; i++)
        {
            collection.add((char) (from + i));
        }
    }

    public static <T> void range(Collection<T> collection, T... chars)
    {
        for (int i = 0; i < chars.length; i++)
        {
            collection.add(chars[i]);
        }
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