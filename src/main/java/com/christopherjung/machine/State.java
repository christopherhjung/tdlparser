package com.christopherjung.machine;

import com.christopherjung.regex.TreeNode;
import com.christopherjung.regex.ValueNode;

import java.util.HashMap;
import java.util.HashSet;

public class State<T>
{
    public static int counter = 0;
    private int info = counter++;

    private HashMap<T, State<T>> next = new HashMap<>();

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
        if(accept) sb.append('!');
        sb.append(')');
        sb.append("->");
        for(T cha : next.keySet()){
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
        HashMap<HashSet<ValueNode<T>>, State<T>> states = new HashMap<>();
        State<T> state = compile(node.getFirstPositions(), states);

        System.out.println(new HashSet<>(states.values()));

        return state;
    }

    private static <T> State<T> compile(HashSet<ValueNode<T>> set, HashMap<HashSet<ValueNode<T>>, State<T>> states)
    {
        if (states.containsKey(set))
        {
            return states.get(set);
        }

        boolean isFinish = false;
        HashMap<T, HashSet<ValueNode<T>>> next = new HashMap<>();
        for (ValueNode<T> child : set)
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

        State<T> state = new State<>(isFinish);
        states.put(set, state);
        for (T cha : next.keySet())
        {
            state.put(cha, compile(next.get(cha), states));
        }

        return state;
    }
}
