package com.christopherjung.translator;

import java.util.*;

public class Kernel
{
    private Grammar grammar;

    private int index;
    private Set<BasicItem> items;
    private HashMap<String, Kernel> closure;

    public Kernel(int index, BasicItem item, Grammar grammar)
    {
        this.index = index;
        this.grammar = grammar;

        closure = new HashMap<>();

        items = new HashSet<>();
        items.add(item);
    }

    public Kernel(int index, Set<BasicItem> items, Grammar grammar)
    {
        this.index = index;
        this.grammar = grammar;
        this.items = items;
    }

    public void addItem(BasicItem item)
    {
        items.add(item);
    }

    public HashMap<String, Set<BasicItem>> getClosure()
    {
        HashMap<String, Set<BasicItem>> result = new HashMap<>();
        Collection<BasicItem> copy = items;

        loop:
        for (;;)
        {
            for (BasicItem item : copy)
            {
                if (item.isFinished())
                {
                    continue;
                }

                String key = item.getNextKey();
                BasicItem next = item.next();

                result.computeIfAbsent(key, ($) -> new HashSet<>())
                        .add(next);

                if (grammar.contains(key))
                {
                    Set<BasicItem> test = new HashSet<>();
                    for (Rule rule : grammar.getRule(key))
                    {
                        BasicItem basicItem = new BasicItem(0, rule);

                        test.add(basicItem);
                    }
                    copy = test;
                    continue loop;
                }
            }

            break;
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this) return true;
        if(!(obj instanceof Kernel)) return false;
        Kernel kernel = (Kernel) obj;

        return items.equals(kernel.items);
    }

    @Override
    public int hashCode()
    {
        return items.hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        sb.append(index);
        sb.append(")");

        int i = 0;
        for (BasicItem item : items)
        {
            if (i++ > 0)
            {
                sb.append(" ; ");
            }

            sb.append(item);
        }

        return sb.toString();
    }
}
