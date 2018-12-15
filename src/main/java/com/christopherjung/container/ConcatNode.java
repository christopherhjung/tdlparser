package com.christopherjung.container;

import java.util.Collection;

public class ConcatNode<T> extends BinaryNode<T>
{

    public ConcatNode(TreeNode<T> left, TreeNode<T> right)
    {
        super(left, right);
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("concat(");
        getLeft().toRegEx(sb);
        sb.append(' ');
        getRight().toRegEx(sb);
        sb.append(")");
    }

    public static <T> TreeNode<T> of(T... nodes)
    {
        TreeNode<T> result = null;
        for (T node : nodes)
        {
            if (result == null)
            {
                result = new ValueNode<>(node);
            }
            else
            {
                result = new ConcatNode<>(result, new ValueNode<>(node));
            }
        }
        return result;
    }
}