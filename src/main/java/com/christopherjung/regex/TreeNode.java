package com.christopherjung.regex;

import java.util.Collection;
import java.util.HashSet;

public abstract class TreeNode<T> implements Cloneable
{
    private boolean nullable;
    private HashSet<ValueNode<T>> firstPositions = new HashSet<>();
    private HashSet<ValueNode<T>> lastPositions = new HashSet<>();
    private HashSet<ValueNode<T>> followPositions = new HashSet<>();

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

    public HashSet<ValueNode<T>> getFirstPositions()
    {
        return firstPositions;
    }

    public HashSet<ValueNode<T>> getLastPositions()
    {
        return lastPositions;
    }

    public HashSet<ValueNode<T>> getFollowPositions()
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
}
