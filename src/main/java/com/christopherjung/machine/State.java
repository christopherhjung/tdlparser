package com.christopherjung.machine;

import com.christopherjung.nda.NDA;
import com.christopherjung.container.TreeNode;

import java.util.*;

public class State<T>
{
    public static int counter = 0;
    private int info = counter++;

    private HashMap<T, State<T>> next = new LinkedHashMap<>();

    private boolean accept;

    public State()
    {
        accept = false;
    }

    public State(boolean isFinish)
    {
        this.accept = isFinish;
    }

    public boolean isAccept()
    {
        return accept;
    }

    public void put(T cha, State<T> state)
    {
        next.put(cha, state);
    }

    public State<T> propagate(T cha)
    {
        return next.get(cha);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append('(');
        sb.append(info);
        if (accept) sb.append('!');
        sb.append(')');
        sb.append("->");
        for (T cha : next.keySet())
        {
            sb.append(cha);
            sb.append(":");
            sb.append(next.get(cha).info);
            sb.append(',');
        }
        sb.append('\n');

        return sb.toString();
    }

    public static <T> State<T> compile(TreeNode<T> node)
    {
        HashMap<Collection<Integer>, State<T>> states = new HashMap<>();
        NDA<T> nda = new NDA<>();
        nda.from(node);

        State<T> state = compile(nda, nda.getFirstPositions(), states);

        //System.out.println(states.values());
        return state;
    }

    private static <T> State<T> compile(NDA<T> nda, Collection<Integer> set, HashMap<Collection<Integer>, State<T>> states)
    {
        if (states.containsKey(set))
        {
            return states.get(set);
        }

        boolean isFinish = false;
        Map<T, HashSet<Integer>> next = new LinkedHashMap<>();
        for (Integer key : set)
        {
            T child = nda.getValues(key);
            if (child != null)
            {
                next.computeIfAbsent(child, ($) -> new HashSet<>())
                        .addAll(nda.getFollowPositions(key));
            }
            else
            {
                isFinish = true;
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
