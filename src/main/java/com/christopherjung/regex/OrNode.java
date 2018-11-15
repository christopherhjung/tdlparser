package com.christopherjung.regex;

import java.util.Collection;

public class OrNode<T> extends TreeNode<T>
{
    private TreeNode<T> left;
    private TreeNode<T> right;

    public OrNode(TreeNode<T> left, TreeNode<T> right)
    {
        this.left = left;
        this.right = right;
        setNullable(left.isNullable() || right.isNullable());

        addFirstPositions(left.getFirstPositions());
        addFirstPositions(right.getFirstPositions());

        addLastPositions(left.getLastPositions());
        addLastPositions(right.getLastPositions());
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(");
        left.toRegEx(sb);
        sb.append('|');
        right.toRegEx(sb);
        sb.append(')');
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


    @Override
    public OrNode clone()
    {
        return new OrNode<>(left.clone(), right.clone());
    }
}