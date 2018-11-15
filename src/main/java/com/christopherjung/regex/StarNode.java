package com.christopherjung.regex;

public class StarNode extends TreeNode
{
    private TreeNode node;

    public StarNode(TreeNode node)
    {
        this.node = node;

        setNullable(true);

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
    public StarNode clone()
    {
        return new StarNode(node.clone());
    }
} 