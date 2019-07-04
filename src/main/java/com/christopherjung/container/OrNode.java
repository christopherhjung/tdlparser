package com.christopherjung.container;

import java.util.Collection;

public class OrNode<T> extends BinaryNode<T>
{
    public OrNode(TreeNode<T> left, TreeNode<T> right)
    {
        super(left, right);
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        getLeft().toRegEx(sb);
        sb.append('|');
        getRight().toRegEx(sb);
    }

    public static <T> TreeNode<T> all(Collection<T> nodes)
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
                result = new OrNode<>(result, new ValueNode<>(node));
            }
        }
        return result;
    }
}