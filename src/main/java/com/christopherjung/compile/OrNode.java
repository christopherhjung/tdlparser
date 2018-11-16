package com.christopherjung.compile;


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
    }

    @Override
    public OrNode<T> clone()
    {
        return new OrNode<>(left.clone(), right.clone());
    }
}