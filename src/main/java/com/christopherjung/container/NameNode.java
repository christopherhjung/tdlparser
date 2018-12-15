package com.christopherjung.container;

public class NameNode<T> extends UnaryNode<T>
{
    private String name;

    public NameNode(String name, TreeNode<T> value)
    {
        super(value);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append(name);
        sb.append(':');
        if (getValue() instanceof ValueNode)
        {
            getValue().toRegEx(sb);
        }
        else
        {
            sb.append('(');
            getValue().toRegEx(sb);
            sb.append(')');
        }

    }
}