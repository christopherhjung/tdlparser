package com.christopherjung.translator;

public class BasicItem
{
    private Rule rule;
    private int dotIndex;


    public BasicItem(int dotIndex, Rule rule)
    {
        this.rule = rule;
        this.dotIndex = dotIndex;
    }

    public String getNextKey()
    {
        return rule.getKey(dotIndex);
    }

    public BasicItem next()
    {
        return new BasicItem(dotIndex + 1, rule);
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

        return dotIndex == item.dotIndex && rule.equals(item.rule);
    }

    @Override
    public int hashCode()
    {
        return rule.hashCode() * (dotIndex << 3);
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

        return sb.toString();
    }
}
