package com.christopherjung.regex;

public class PlusNode<T> extends TreeNode<T>
{
    private TreeNode<T> node;

    public PlusNode(TreeNode<T> node)
    {
        this.node = node;

        setNullable(false);

        addFirstPositions(node.getFirstPositions());
        addLastPositions(node.getLastPositions());

        for (TreeNode<T> child : node.getLastPositions())
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
    public PlusNode<T> clone()
    {
        return new PlusNode<>(node.clone());
    }
} 