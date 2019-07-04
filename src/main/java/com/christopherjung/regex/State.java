package com.christopherjung.regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class State<T>
{
    public static int counter = 0;
    private int info = counter++;

    private HashMap<T, State<T>> next = new LinkedHashMap<>();
    private Set<T> lookahead = new HashSet<>();

    private boolean accept;

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

    public State(boolean accept, Set<T> lookahead)
    {
        this.accept = accept;
        this.lookahead = lookahead;
    }

    public void setAccept(boolean accept)
    {
        this.accept = accept;
    }

    public Set<T> getLookahead()
    {
        return lookahead;
    }

    public void setLookahead(Set<T> lookahead)
    {
        this.lookahead = lookahead;
    }

    public boolean isAccept()
    {
        return accept;
    }

    public boolean isLookahead(T element)
    {
        return lookahead == null ? false : lookahead.contains(element);
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


        if (lookahead != null && lookahead.size() > 0)
        {
            sb.append(lookahead);
        }

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
