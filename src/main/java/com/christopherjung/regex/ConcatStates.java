package com.christopherjung.regex;

import java.util.*;

public class ConcatStates<T>
{
    private HashMap<Set<State<T>>, TokenState<T>> cache = new HashMap<>();


    public static <T> TokenState<T> create(Map<String, State<T>> list)
    {
        return new ConcatStates<T>().test(list, new TokenState<>());
    }

    private TokenState<T> test(Map<String, State<T>> list, TokenState<T> current)
    {
        HashSet<T> values = new HashSet<>();
        boolean accept = false;
        Set<T> lookahead = new HashSet<>();

        for (String token : list.keySet())
        {
            State<T> state = list.get(token);
            if (!accept && state.isAccept())
            {
                current.setToken(token);
                accept = true;
            }
            lookahead.addAll(state.getLookahead());
            values.addAll(state.getNext().keySet());
        }

        current.setAccept(accept);
        current.setLookahead(lookahead);

        for (T value : values)
        {
            LinkedHashMap<String, State<T>> map = new LinkedHashMap<>();

            for (String token : list.keySet())
            {
                State<T> state = list.get(token);
                if (state.getNext().containsKey(value))
                {
                    map.put(token, state.getNext().get(value));
                }
            }

            HashSet<State<T>> set = new HashSet<>(map.values());

            TokenState<T> next;
            if (cache.containsKey(set))
            {
                next = cache.get(set);
            }
            else
            {
                next = new TokenState<>();
                cache.put(set, next);
                test(map, next);
            }

            current.put(value, next);
        }

        return current;
    }
}
