package com.christopherjung.container;

public abstract class BinaryNode<T> extends TreeNode<T>
{
    public enum Type
    {
        CONCAT, OR
    }

    private TreeNode<T> left;
    private TreeNode<T> right;
    private Type type;

    public BinaryNode(TreeNode<T> left, TreeNode<T> right)
    {
        if (left == null || right == null)
        {
            throw new RuntimeException("Null not allowed");
        }

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
