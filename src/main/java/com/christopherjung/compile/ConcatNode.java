package com.christopherjung.compile;

public class ConcatNode<T> extends TreeNode<T>
{
    private TreeNode<T> left;
    private TreeNode<T> right;

    public ConcatNode(TreeNode<T> left, TreeNode<T> right)
    {
        this.left = left;
        this.right = right;

        setNullable(left.isNullable() && right.isNullable());

        addFirstPositions(left.getFirstPositions());
        if (left.isNullable())
        {
            addFirstPositions(right.getFirstPositions());
        }

        if (right.isNullable())
        {
            addLastPositions(left.getLastPositions());
        }
        addLastPositions(right.getLastPositions());

        for (TreeNode<T> child : left.getLastPositions())
        {
            child.addFollowPositions(right.getFirstPositions());
        }
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
    }

    @Override
    public ConcatNode<T> clone()
    {
        return new ConcatNode<>(left.clone(), right.clone());
    }
}