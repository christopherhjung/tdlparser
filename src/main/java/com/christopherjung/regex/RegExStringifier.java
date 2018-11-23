package com.christopherjung.regex;

import com.christopherjung.grammar.*;

import java.util.HashMap;

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
