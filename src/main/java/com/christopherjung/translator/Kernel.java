package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;

import java.util.*;

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
        for (BasicItem item : items)
        {
            if (item.isFinished())
            {
                return item.getRule();
            }
        }
        return null;
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
