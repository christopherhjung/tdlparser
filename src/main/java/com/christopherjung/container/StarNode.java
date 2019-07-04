package com.christopherjung.container;

public class StarNode<T> extends UnaryNode<T>
{
    public StarNode(TreeNode<T> node)
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