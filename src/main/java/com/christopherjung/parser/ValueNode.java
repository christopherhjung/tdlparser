package com.christopherjung.parser;

import java.util.Collection;

public class ValueNode extends TreeNode
{
    private char value;

    public ValueNode(char value)
    {
        this.value = value;
        setNullable(false);

        addFirstPosition(this);
        addLastPosition(this);
    }

    public ValueNode()
    {
        this((char) 255);
    }

    public char getValue()
    {
        return value;
    }

    public boolean isEpsilon()
    {
        return value == 255;
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

    public static TreeNode[] fromTo(int from, int to)
    {
        TreeNode[] result = new TreeNode[to - from + 1];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = new ValueNode((char) (from + i));
        }

        return result;
    }

    public static TreeNode[] map(char... chars)
    {
        TreeNode[] result = new TreeNode[chars.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = new ValueNode(chars[i]);
        }

        return result;
    }


    public static void fromTo(Collection<TreeNode> collection, int from, int to)
    {
        for (int i = 0; i < to - from - 1; i++)
        {
            collection.add(new ValueNode((char) (from + i)));
        }
    }

    public static void map(Collection<TreeNode> collection, char... chars)
    {
        for (int i = 0; i < chars.length; i++)
        {
            collection.add(new ValueNode(chars[i]));
        }
    }

    public static void fromToChars(Collection<Character> collection, int from, int to)
    {
        for (int i = 0; i < to - from - 1; i++)
        {
            collection.add((char) (from + i));
        }
    }

    public static void range(Collection<Character> collection, char... chars)
    {
        for (int i = 0; i < chars.length; i++)
        {
            collection.add(chars[i]);
        }
    }


    @Override
    public ValueNode clone()
    {
        return new ValueNode(value);
    }

    /*
        @Override
        public int hashCode()
        {
            return value;
        }
    */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof ValueNode)) return false;
        ValueNode other = (ValueNode) obj;


        return value == other.value;
    }
}