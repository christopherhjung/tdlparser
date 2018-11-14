package com.christopherjung.machine;

public class Machine
{
    private State begin;

    public Machine(State begin)
    {
        this.begin = begin;
    }

    public boolean check(String str)
    {
        State current = begin;

        for (char cha : str.toCharArray())
        {
            current = current.propagate(cha);
            if (current == null)
            {
                return false;
            }
        }

        return current.isAccept();
    }

    public static Machine compile(String regEx)
    {
        return new Machine(Pattern.compile(regEx));
    }

}
