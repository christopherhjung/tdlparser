package com.christopherjung.compile;


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