package com.christopherjung.regex;

import com.christopherjung.container.TreeNode;
import com.christopherjung.nda.NDA;

import java.util.*;

public class AdvancedState
{
    public static int counter = 0;
    private int info = counter++;

    private Object tag;
    private HashMap<Character, AdvancedState> next = new LinkedHashMap<>();

    private boolean accept;

    public AdvancedState()
    {
        accept = false;
    }

    public AdvancedState(boolean isFinish)
    {
        this.accept = isFinish;
    }

    public boolean isAccept()
    {
        return accept;
    }

    public AdvancedState propagate(Character cha)
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
        for (Character cha : next.keySet())
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
