package com.christopherjung.regex;

import java.util.*;

public class State<T>
{
    public static int counter = 0;
    private int info = counter++;

    private HashMap<T, State<T>> next = new LinkedHashMap<>();

    private boolean accept;
    private boolean lookahead;

    public State()
    {
    }

    public State(HashMap<T, State<T>> next)
    {
        this.next = next;
    }

    public State(boolean isFinish)
    {
        this.accept = isFinish;
    }

    public State(boolean accept, boolean lookahead)
    {
        this.accept = accept;
        this.lookahead = lookahead;
    }

    public void setAccept(boolean accept)
    {
        this.accept = accept;
    }

    public boolean isAccept()
    {
        return accept;
    }

    public boolean isLookahead()
    {
        return lookahead;
    }

    public HashMap<T, State<T>> getNext()
    {
        return next;
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
        if (lookahead) sb.append('?');
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
