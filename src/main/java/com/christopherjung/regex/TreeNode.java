package com.christopherjung.regex;

import java.util.Collection;
import java.util.HashSet;

public abstract class TreeNode<T> implements Cloneable
{
    public String toRegEx()
    {
        StringBuilder sb = new StringBuilder();
        toRegEx(sb);
        return sb.toString();
    }

    protected abstract void toRegEx(StringBuilder sb);

    public abstract TreeNode<T> clone();
}
