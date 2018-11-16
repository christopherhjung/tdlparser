package com.christopherjung.machine;

import com.christopherjung.regex.RegExParser;
import com.christopherjung.regex.TreeNode;
import com.christopherjung.regex.ValueNode;

import java.util.HashMap;
import java.util.HashSet;

public class Pattern
{
    public static State<Character> compile(String regEx)
    {
        RegExParser parser = new RegExParser();
        TreeNode<Character> parsed = parser.parse(regEx);
        return State.compile(parsed);
    }
}
