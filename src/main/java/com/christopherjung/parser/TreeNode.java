package com.christopherjung.parser;

import java.util.Collection;
import java.util.HashSet;

public abstract class TreeNode implements Cloneable
{
    private boolean nullable;
    private HashSet<ValueNode> firstPositions = new HashSet<>();
    private HashSet<ValueNode> lastPositions = new HashSet<>();
    private HashSet<ValueNode> followPositions = new HashSet<>();

    public boolean isNullable()
    {
        return nullable;
    }

    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    protected void addFirstPositions(Collection<ValueNode> collection)
    {
        firstPositions.addAll(collection);
    }

    protected void addLastPositions(Collection<ValueNode> collection)
    {
        lastPositions.addAll(collection);
    }

    protected void addFollowPositions(Collection<ValueNode> collection)
    {
        followPositions.addAll(collection);
    }

    protected void addFirstPosition(ValueNode valueNode)
    {
        firstPositions.add(valueNode);
    }

    protected void addLastPosition(ValueNode valueNode)
    {
        lastPositions.add(valueNode);
    }

    protected void addFollowPosition(ValueNode valueNode)
    {
        followPositions.add(valueNode);
    }

    public HashSet<ValueNode> getFirstPositions()
    {
        return firstPositions;
    }

    public HashSet<ValueNode> getLastPositions()
    {
        return lastPositions;
    }

    public HashSet<ValueNode> getFollowPositions()
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

    public abstract TreeNode clone();
}
