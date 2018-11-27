package com.christopherjung.container;

public abstract class TreeNode<T> implements Cloneable
{
    public String toRegEx()
    {
        StringBuilder sb = new StringBuilder();
        toRegEx(sb);
        return sb.toString();
    }

    protected abstract void toRegEx(StringBuilder sb);

    public static <T> TreeNode<T> close(TreeNode<T> root)
    {
        return new ConcatNode<>(root, new ValueNode<>());
    }

}
