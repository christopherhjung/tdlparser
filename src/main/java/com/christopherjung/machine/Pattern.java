package com.christopherjung.machine;

import com.christopherjung.regex.RegExParser;
import com.christopherjung.grammar.TreeNode;

public class Pattern
{
    public static State<Character> compile(String regEx)
    {
        RegExParser parser = new RegExParser();
        TreeNode<Character> parsed = parser.parse(regEx);
        return State.compile(parsed);
    }
}
