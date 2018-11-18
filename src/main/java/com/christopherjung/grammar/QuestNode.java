package com.christopherjung.grammar;

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

} 