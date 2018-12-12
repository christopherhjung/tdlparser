package com.christopherjung.container;

public class NegativeLookaheadNode<T> extends UnaryNode<T>
{
    public NegativeLookaheadNode(TreeNode<T> node)
    {
        super(node);
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(?!");
        getValue().toRegEx(sb);
        sb.append(")");
    }

} 