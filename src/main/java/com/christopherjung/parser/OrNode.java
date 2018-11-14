package com.christopherjung.parser;

import java.util.Collection;

public class OrNode extends TreeNode
{
    private TreeNode left;
    private TreeNode right;

    public OrNode(TreeNode left, TreeNode right)
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

    public static TreeNode all(Collection<Character> nodes)
    {
        TreeNode result = null;
        for (Character node : nodes)
        {
            if (result == null)
            {
                result = new ValueNode(node);
            }
            else
            {
                result = new OrNode(result, new ValueNode(node));
            }
        }
        return result;
    }

    public static TreeNode all(TreeNode... nodes)
    {
        TreeNode result = null;
        for (TreeNode node : nodes)
        {
            if (result == null)
            {
                result = node;
            }
            else
            {
                result = new OrNode(result, node);
            }
        }
        return result;
    }


    @Override
    public OrNode clone()
    {
        return new OrNode(left.clone(),right.clone());
    }
}