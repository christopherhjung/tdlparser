package com.christopherjung.translator;

import java.util.HashSet;

public class BasicItem
{
    private Rule rule;
    private int dotIndex;
    private HashSet<String> lookahead;

    public BasicItem(int dotIndex, Rule rule, HashSet<String> lookahead)
    {
        this.rule = rule;
        this.dotIndex = dotIndex;
        this.lookahead = lookahead;
    }

    public BasicItem(int dotIndex, Rule rule)
    {
        this(dotIndex, rule, new HashSet<>());
    }

    public HashSet<String> getLookahead()
    {
        return lookahead;
    }

    public String getNextKey()
    {
        return rule.getKey(dotIndex);
    }

    public int getDotIndex()
    {
        return dotIndex;
    }

    public Rule getRule()
    {
        return rule;
    }

    public BasicItem next()
    {
        if (isFinished())
        {
            throw new RuntimeException("No further Item possible");
        }

        return new BasicItem(dotIndex + 1, rule, lookahead);
    }

    public boolean isFinished()
    {
        return dotIndex == rule.size();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof BasicItem)) return false;
        BasicItem item = (BasicItem) obj;

        return dotIndex == item.dotIndex && rule.equals(item.rule) && lookahead.equals(item.lookahead);
    }

    @Override
    public int hashCode()
    {
        return (rule.hashCode() * (dotIndex << 3));// ^ lookahead.hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(rule.getName());
        sb.append("->");

        for (int i = 0; i < rule.size(); i++)
        {
            if (i == dotIndex)
            {
                sb.append('.');
            }
            else if (i > 0)
            {
                sb.append(' ');
            }

            sb.append(rule.getKey(i));
        }

        if (isFinished())
        {
            sb.append('.');
        }

        sb.append(" ");
        sb.append(lookahead);

        return sb.toString();
    }
}
