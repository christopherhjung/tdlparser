package com.christopherjung.compile;


import java.util.*;

public class CompilerState<T>
{

    public static int counter = 0;
    private int info = counter++;

    private Map<T, CompilerState<T>> next = new LinkedHashMap<>();

    private boolean accept;

    public CompilerState()
    {
        accept = false;
    }

    public CompilerState(boolean isFinish)
    {
        this.accept = isFinish;
    }

    public boolean isAccept()
    {
        return accept;
    }

    public void put(T cha, CompilerState<T> state)
    {
        next.put(cha, state);
    }

    public CompilerState<T> propagate(T cha)
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

    public static <T> CompilerState<T> compile(TreeNode<T> node)
    {
        HashMap<List<ValueNode<T>>, CompilerState<T>> states = new HashMap<>();
        CompilerState<T> state = compile(new ArrayList<>(node.getFirstPositions()), states);

        System.out.println("--------------------");
        System.out.println(node.getFirstPositions());

        System.out.println(new HashSet<>(states.values()));

        return state;
    }

    private static <T> CompilerState<T> compile(List<ValueNode<T>> set, HashMap<List<ValueNode<T>>, CompilerState<T>> states)
    {
        if (states.containsKey(set))
        {
            return states.get(set);
        }

        boolean isFinish = false;
        Map<T, List<ValueNode<T>>> next = new LinkedHashMap<>();
        for (ValueNode<T> child : set)
        {
            if (!child.isEpsilon())
            {
                next.computeIfAbsent(child.getValue(), (key) -> new ArrayList<>())
                        .addAll(child.getFollowPositions());
            }
            else
            {
                isFinish = true;
            }
        }

        CompilerState<T> compilerState = new CompilerState<>(isFinish);
        states.put(set, compilerState);
        for (T key : next.keySet())
        {
            compilerState.put(key, compile(next.get(key), states));
        }

        return compilerState;
    }
}
