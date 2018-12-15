package com.christopherjung.container;

public abstract class UnaryNode<T> extends TreeNode<T>
{
    private TreeNode<T> value;

    public UnaryNode(TreeNode<T> value)
    {
        this.value = value;
    }

    public TreeNode<T> getValue()
    {
        return value;
    }


}
