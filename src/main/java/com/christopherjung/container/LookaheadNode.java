package com.christopherjung.container;

public class LookaheadNode<T> extends UnaryNode<T>
{
    public LookaheadNode(TreeNode<T> node)
    {
        super(node);
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(?=");
        getValue().toRegEx(sb);
        sb.append(")");
    }

} 