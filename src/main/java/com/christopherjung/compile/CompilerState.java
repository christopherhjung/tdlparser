package com.christopherjung.compile;


import java.util.LinkedHashMap;
import java.util.Map;

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

}
