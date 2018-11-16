package com.christopherjung.regex;

import java.util.HashSet;

public class StarNode<T> extends TreeNode<T>
{
    private TreeNode<T> node;

    public StarNode(TreeNode<T> node)
    {
        this.node = node;

        setNullable(true);

        addFirstPositions(node.getFirstPositions());
        addLastPositions(node.getLastPositions());

        HashSet<ValueNode<T>> valueNode =  node.getLastPositions();

        for (ValueNode<T> child : valueNode)
        {
            child.addFollowPositions(getFirstPositions());
        }
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(");
        node.toRegEx(sb);
        sb.append(")*");
    }

    @Override
    public StarNode<T> clone()
    {
        return new StarNode<>(node.clone());
    }
} 