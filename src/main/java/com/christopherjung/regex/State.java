package com.christopherjung.regex;

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
}
