package com.christopherjung.container;

public class PlusNode<T> extends UnaryNode<T>
{
    public PlusNode(TreeNode<T> node)
    {
        super(node);
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(");
        getValue().toRegEx(sb);
        sb.append(")*");
    }
} 