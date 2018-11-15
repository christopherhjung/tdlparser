package com.christopherjung.regex;

public class ConcatNode extends TreeNode
{
    private TreeNode left;
    private TreeNode right;

    public ConcatNode(TreeNode left, TreeNode right)
    {
        this.left = left;
        this.right = right;

        setNullable(left.isNullable() && right.isNullable());

        addFirstPositions(left.getFirstPositions());
        if (left.isNullable())
        {
            addFirstPositions(right.getFirstPositions());
        }

        addLastPositions(right.getLastPositions());
        if (right.isNullable())
        {
            addLastPositions(left.getLastPositions());
        }

        for (TreeNode child : left.getLastPositions())
        {
            child.addFollowPositions(right.getFirstPositions());
        }
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(");
        left.toRegEx(sb);
        right.toRegEx(sb);
        sb.append(')');
    }

    @Override
    public ConcatNode clone()
    {
        return new ConcatNode(left.clone(),right.clone());
    }
}