package com.christopherjung.regex;

public class Machine<T>
{
    private State<T> begin;

    public Machine(State<T> begin)
    {
        this.begin = begin;
    }

    public boolean check(T... str)
    {
        State<T> current = begin;

        for (T key : str)
        {
            current = current.propagate(key);
            if (current == null)
            {
                return false;
            }
        }

        return current.isAccept();
    }

    public static Machine<Character> compile(String regEx)
    {
        return new Machine<>(Pattern.compile(regEx));
    }

}
