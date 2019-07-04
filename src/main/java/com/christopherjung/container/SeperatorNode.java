package com.christopherjung.container;

public class SeperatorNode<T> extends TreeNode<T>
{
    private TreeNode<T> target;
    private String separator;

    public SeperatorNode(TreeNode<T> target, String separator)
    {
        this.target = target;
        this.separator = separator;
    }

    public String getSeparator()
    {
        return separator;
    }

    public TreeNode<T> getTarget()
    {
        return target;
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        target.toRegEx(sb);
        sb.append('[');
        sb.append(separator);
        sb.append(']');
    }
}
