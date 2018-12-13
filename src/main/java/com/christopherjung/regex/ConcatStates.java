package com.christopherjung.regex;

import java.util.*;

public class ConcatStates<T>
{
    private HashMap<Set<State<T>>, State<T>> cache = new HashMap<>();


    public static <T> State<T> create(Collection<State<T>> list)
    {
        System.out.println(list);
        var concat = new ConcatStates<T>();
        State<T> result = concat.test(list, new State<>());
        System.out.println(result);
        System.out.println(concat.cache.values());

        return result;
    }

    private State<T> test(Collection<State<T>> list, State<T> current)
    {

        HashSet<T> values = new HashSet<>();
        boolean accept = true;

        for (State<T> state : list)
        {
            accept &= state.isAccept();
            values.addAll(state.getNext().keySet());
        }

        current.setAccept(accept);

        for (T value : values)
        {
            HashSet<State<T>> set = new HashSet<>();

            for (State<T> state : list)
            {
                if (state.getNext().containsKey(value))
                {
                    set.add(state.getNext().get(value));
                }
            }

            State<T> next;
            if (cache.containsKey(set))
            {
                next = cache.get(set);
            }
            else
            {
                next = new State<>();
                cache.put(set, next);
                test(set, next);
            }

            current.put(value, next);
        }

        return current;
    }
}
