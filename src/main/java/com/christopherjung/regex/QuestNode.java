package com.christopherjung.regex;

public class QuestNode<T> extends TreeNode<T>
{
    private TreeNode<T> node;

    public QuestNode(TreeNode<T> node)
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
    public QuestNode<T> clone()
    {
        return new QuestNode<>(node.clone());
    }
} 