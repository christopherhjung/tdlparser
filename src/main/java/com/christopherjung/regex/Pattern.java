package com.christopherjung.regex;

import com.christopherjung.container.TreeNode;

public class Pattern
{
    public static State<Character> compile(String regEx)
    {
        RegExParser parser = new RegExParser();
        TreeNode<Character> parsed = parser.parse(regEx);
        return State.compile(parsed);
    }
}
