package com.christopherjung.regex;

public class Machine
{
    private State<Character> begin;

    public Machine(State<Character> begin)
    {
        this.begin = begin;
    }

    public boolean check(String str)
    {
        State<Character> current = begin;

        for (char key : str.toCharArray())
        {
            current = current.propagate(key);
            if (current == null)
            {
                return false;
            }
        }

        return current.isAccept();
    }

}
