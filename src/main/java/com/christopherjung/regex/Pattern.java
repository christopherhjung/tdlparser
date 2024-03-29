package com.christopherjung.regex;

import com.christopherjung.container.TreeNode;
import com.christopherjung.nda.NDA;

import java.util.*;

public class Pattern
{
    public static State<Character> compile(String regEx)
    {
        RegExParser parser = new RegExParser();
        TreeNode<Character> parsed = parser.parse(regEx);
        return compile(parsed);
    }

    public static <T> State<T> compile(TreeNode<T> node)
    {
        NDA<T> nda = NDA.create(node);

        HashMap<Set<Integer>, State<T>> states = new HashMap<>();
        State<T> state = compile(nda, nda.getFirstPositions(), states);

        System.out.println(states.values());

        return state;
    }

    private static <T> State<T> compile(NDA<T> nda, Set<Integer> set, HashMap<Set<Integer>, State<T>> states)
    {
        if (states.containsKey(set))
        {
            return states.get(set);
        }

        boolean isFinish = false;
        Map<T, HashSet<Integer>> next = new LinkedHashMap<>();
        for (Integer key : set)
        {
            if (nda.isFinish(key))
            {
                isFinish = true;
            }
            else
            {
                T child = nda.getValue(key);
                next.computeIfAbsent(child, ($) -> new HashSet<>())
                        .addAll(nda.getFollowPositions(key));
            }
        }

        State<T> state = new State<>(isFinish);
        states.put(set, state);
        for (T key : next.keySet())
        {
            state.put(key, compile(nda, next.get(key), states));
        }

        return state;
    }
}
