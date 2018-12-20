package com.christopherjung.regex;

import com.christopherjung.container.TreeNode;

import java.util.*;

public class Pattern<T>
{
    public static State<Character> compile(String regEx)
    {
        RegExParser parser = new RegExParser();
        TreeNode<Character> parsed = parser.parse(regEx);
        return compile(parsed);
    }

    public static <T> State<T> compile(TreeNode<T> node)
    {
        //System.out.println("gen  nda");
        NDA<T> nda = NDA.create(node);

        Pattern<T> pattern = new Pattern<>(nda);

        //System.out.println("nda ready");

        State<T> state = pattern.compile(nda.getFirstPositions());

        //System.out.println(pattern.states.values());

        return state;
    }

    private HashMap<Set<Integer>, State<T>> states = new HashMap<>();
    private HashMap<Map<T, HashSet<Integer>>, State<T>> cache = new HashMap<>();
    private NDA<T> nda;

    public Pattern(NDA<T> nda)
    {
        this.nda = nda;
    }

    private State<T> compile(Set<Integer> set)
    {
        if (states.containsKey(set))
        {
            return states.get(set);
        }

        boolean isFinish = false;
        Set<T> lookahead = new HashSet<>();
        Map<T, HashSet<Integer>> next = new HashMap<>();
        for (Integer key : set)
        {
            if (nda.isFinish(key))
            {
                isFinish = true;
            }

            T child = nda.getValue(key);
            if (child != null)
            {
                if (lookahead.contains(child))
                {
                    if (!nda.isLookahead(key))
                    {
                        throw new RuntimeException("runndnsfsd");
                    }
                }
                else if (nda.isLookahead(key))
                {
                    lookahead.add(child);
                }

                next.computeIfAbsent(child, ($) -> new HashSet<>()).addAll(nda.getFollowPositions(key));
            }
        }

        /*
        if (cache.containsKey(next))
        {
            return cache.get(next);
        }*/

        State<T> state = new State<>(isFinish, lookahead);
        states.put(set, state);
        cache.put(next, state);
        for (T key : next.keySet())
        {
            state.put(key, compile(next.get(key)));
        }

        return state;
    }
}
