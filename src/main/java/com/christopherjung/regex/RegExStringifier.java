package com.christopherjung.regex;

import com.christopherjung.container.*;

public class RegExStringifier
{
    private StringBuilder builder = new StringBuilder();

    public void visit(TreeNode<Character> node)
    {

    }

    @Override
    public String toString()
    {
        return builder.toString();
    }
}
