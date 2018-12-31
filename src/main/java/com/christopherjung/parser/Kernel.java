package com.christopherjung.parser;

import java.util.HashSet;
import java.util.Set;

public class Kernel
{
    private Set<BasicItem> items;

    public Kernel(Set<BasicItem> items)
    {
        this.items = items;
    }

    public Kernel(BasicItem item)
    {
        items = new HashSet<>();
        items.add(item);
    }

    public Set<BasicItem> getItems()
    {
        return items;
    }

    public Rule getFinished()
    {
        Rule finishRule = null;

        for (BasicItem item : items)
        {
            if (item.isFinished())
            {
                finishRule = item.getRule();
                break;
            }
        }
        return finishRule;
    }

    public Set<BasicItem> getFinishedItems()
    {
        Set<BasicItem> result = new HashSet<>();
        for (BasicItem item : items)
        {
            if (item.isFinished())
            {
                result.add(item);
            }
        }
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof Kernel)
        {
            Kernel kernel = (Kernel) obj;

            return items.equals(kernel.items);
        }

        if (obj instanceof Set)
        {
            Set<?> items = (Set<?>) obj;

            return items.equals(this.items);
        }

        return false;
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
