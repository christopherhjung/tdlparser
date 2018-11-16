package com.christopherjung.compile;

import java.util.Collection;
import java.util.ArrayList;

public abstract class TreeNode<T> implements Cloneable
{
    private boolean nullable;
    private ArrayList<ValueNode<T>> firstPositions = new ArrayList<>();
    private ArrayList<ValueNode<T>> lastPositions = new ArrayList<>();
    private ArrayList<ValueNode<T>> followPositions = new ArrayList<>();

    public boolean isNullable()
    {
        return nullable;
    }

    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    protected void addFirstPositions(Collection<ValueNode<T>> collection)
    {
        firstPositions.addAll(collection);
    }

    protected void addLastPositions(Collection<ValueNode<T>> collection)
    {
        lastPositions.addAll(collection);
    }

    protected void addFollowPositions(Collection<ValueNode<T>> collection)
    {
        followPositions.addAll(collection);
    }

    protected void addFirstPosition(ValueNode<T> valueNode)
    {
        firstPositions.add(valueNode);
    }

    protected void addLastPosition(ValueNode<T> valueNode)
    {
        lastPositions.add(valueNode);
    }

    protected void addFollowPosition(ValueNode<T> valueNode)
    {
        followPositions.add(valueNode);
    }

    public ArrayList<ValueNode<T>> getFirstPositions()
    {
        return firstPositions;
    }

    public ArrayList<ValueNode<T>> getLastPositions()
    {
        return lastPositions;
    }

    public ArrayList<ValueNode<T>> getFollowPositions()
    {
        return followPositions;
    }

    public String toRegEx()
    {
        StringBuilder sb = new StringBuilder();
        toRegEx(sb);
        return sb.toString();
    }

    protected abstract void toRegEx(StringBuilder sb);

    public abstract TreeNode<T> clone();

    public static <T> TreeNode<T> close(TreeNode<T> root)
    {
        return new ConcatNode<>(root, new ValueNode<>());
    }
}
