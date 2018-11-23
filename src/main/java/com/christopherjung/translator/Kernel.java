package com.christopherjung.translator;

import java.util.*;

public class Kernel
{
    private Grammar grammar;

    private int index;
    private Set<BasicItem> items;
    private HashMap<String, Kernel> closure;
    private ClosureTable closureTable;

    public Kernel(Set<BasicItem> items, ClosureTable closureTable)
    {
        this.closureTable = closureTable;
        grammar = closureTable.getGrammar();
        this.items = items;
    }

    public Kernel(BasicItem item, ClosureTable closureTable)
    {
        this.closureTable = closureTable;
        grammar = closureTable.getGrammar();
        items = new HashSet<>();
        items.add(item);
    }

    public Set<BasicItem> getItems()
    {
        return items;
    }

    public Rule getFinished()
    {
        for (BasicItem item : items)
        {
            if (item.isFinished())
            {
                return item.getRule();
            }
        }
        return null;
    }

    public HashMap<String, Kernel> getClosure()
    {
        if (closure == null)
        {
            initClosure();
        }

        return closure;
    }

    public void initClosure()
    {
        HashMap<String, Set<BasicItem>> result = new LinkedHashMap<>();
        HashSet<BasicItem> visited = new HashSet<>(items);
        LinkedList<BasicItem> current = new LinkedList<>(items);

        for (; !current.isEmpty(); )
        {
            BasicItem item = current.remove();
            if (item.isFinished())
            {
                continue;
            }

            String key = item.getNextKey();
            BasicItem nextItem = item.next();

            result.computeIfAbsent(key, ($) -> new HashSet<>()).add(nextItem);

            if (grammar.contains(key))
            {
                for (Rule rule : grammar.getRule(key))
                {
                    BasicItem newItem = new BasicItem(0, rule);
                    if (!visited.contains(newItem))
                    {
                        current.add(newItem);
                        visited.add(newItem);
                    }
                }
            }
        }

        HashMap<String, Kernel> transform = new HashMap<>();

        for (String symbol : result.keySet())
        {
            transform.put(symbol, closureTable.getOrCreateKernel(result.get(symbol)));
        }

        closure = transform;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof Kernel)) return false;
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
