package com.christopherjung.regex;

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

    @Override
    public PlusNode<T> clone()
    {
        return new PlusNode<>(getValue().clone());
    }
} 