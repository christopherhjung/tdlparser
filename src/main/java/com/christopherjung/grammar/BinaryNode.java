package com.christopherjung.grammar;

public abstract class BinaryNode<T> extends TreeNode<T>
{
    private TreeNode<T> left;
    private TreeNode<T> right;

    public BinaryNode(TreeNode<T> left, TreeNode<T> right)
    {
        this.left = left;
        this.right = right;
    }

    public TreeNode<T> getLeft()
    {
        return left;
    }

    public TreeNode<T> getRight()
    {
        return right;
    }
}
