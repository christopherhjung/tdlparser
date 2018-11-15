package com.christopherjung.regex;

public class QuestNode extends TreeNode
{
    private TreeNode node;

    public QuestNode(TreeNode node)
    {
        this.node = node;

        setNullable(true);

        addFirstPositions(node.getFirstPositions());
        addLastPositions(node.getLastPositions());
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(");
        node.toRegEx(sb);
        sb.append(")?");
    }

    @Override
    public QuestNode clone()
    {
        return new QuestNode(node.clone());
    }
} 