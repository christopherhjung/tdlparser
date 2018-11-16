package com.christopherjung.regex;

public class QuestNode<T> extends UnaryNode<T>
{
    public QuestNode(TreeNode<T> node)
    {
        super(node);

    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(");
        getValue().toRegEx(sb);
        sb.append(")?");
    }

    @Override
    public QuestNode<T> clone()
    {
        return new QuestNode<>(getValue().clone());
    }
} 