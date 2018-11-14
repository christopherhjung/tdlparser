package com.christopherjung.machine;

import com.christopherjung.parser.RegExParser;
import com.christopherjung.parser.TreeNode;
import com.christopherjung.parser.ValueNode;

import java.util.HashMap;
import java.util.HashSet;

public class Pattern
{
    public static State compile(String regEx)
    {
        RegExParser parser = new RegExParser();
        TreeNode parsed = parser.parse(regEx);
        //System.out.println(parsed.toRegEx());
        return compile(parsed);
    }

    public static State compile(TreeNode node)
    {
        HashMap<HashSet<ValueNode>, State> states = new HashMap<>();
        State state = compile(node.getFirstPositions(), states);

        //System.out.println(new HashSet<>(states.values()));

        return state;
    }

    private static State compile(HashSet<ValueNode> set, HashMap<HashSet<ValueNode>, State> states)
    {
        if (states.containsKey(set))
        {
            return states.get(set);
        }

        boolean isFinish = false;
        HashMap<Character, HashSet<ValueNode>> next = new HashMap<>();
        for (ValueNode child : set)
        {
            if (!child.isEpsilon())
            {
                next.computeIfAbsent(child.getValue(), (key) -> new HashSet<>())
                        .addAll(child.getFollowPositions());
            }
            else
            {
                isFinish = true;
            }
        }

        State state = new State(isFinish);
        states.put(set, state);
        for (char cha : next.keySet())
        {
            state.put(cha, compile(next.get(cha), states));
        }

        return state;
    }
}
