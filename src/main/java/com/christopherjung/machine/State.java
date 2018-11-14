package com.christopherjung.machine;

import java.util.HashMap;

public class State
{
    public static int counter = 0;
    private int info = counter++;

    private HashMap<Character, State> next = new HashMap<>();

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

    public void put(char cha, State state)
    {
        next.put(cha, state);
    }

    public State propagate(char cha)
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
        for(char cha : next.keySet()){
            sb.append(cha);
            sb.append(":");
            sb.append(next.get(cha).info);
            sb.append(',');
        }
        sb.append('\n');

        return sb.toString();
    }
}
