package com.christopherjung.regex;

public class ConcatNode<T> extends BinaryNode<T>
{

    public ConcatNode(TreeNode<T> left, TreeNode<T> right)
    {
        super(left, right);
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        sb.append("(");
        getLeft().toRegEx(sb);
        getRight().toRegEx(sb);
        sb.append(')');
    }

    @Override
    public ConcatNode<T> clone()
    {
        return new ConcatNode<>(getLeft().clone(), getRight().clone());
    }
}