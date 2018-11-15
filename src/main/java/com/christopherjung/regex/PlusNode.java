package com.christopherjung.regex;

public class PlusNode extends TreeNode
{
    private TreeNode node;

    public PlusNode(TreeNode node)
    {
        this.node = node;

        setNullable(false);

        addFirstPositions(node.getFirstPositions());
        addLastPositions(node.getLastPositions());

        for (TreeNode child : node.getLastPositions())
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
    public PlusNode clone()
    {
        return new PlusNode(node.clone());
    }
} 