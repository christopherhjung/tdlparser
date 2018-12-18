package com.christopherjung.container;

import java.util.Map;

public class OptionNode<T> extends TreeNode<T>
{
    private TreeNode<T> target;
    private Map<String, String> options;

    public OptionNode(TreeNode<T> target, Map<String, String> options)
    {
        this.target = target;
        this.options = options;
    }

    public Map<String, String> getOptions()
    {
        return options;
    }

    public String getOption(String key)
    {
        return options.get(key);
    }

    public String getOption(String key, Object defaultValue)
    {
        return options.getOrDefault(key, defaultValue.toString());
    }

    public int getInt(String key, int defaultValue)
    {
        if (options.containsKey(key))
        {
            return Integer.parseInt(options.get(key));
        }

        return defaultValue;
    }

    public TreeNode<T> getTarget()
    {
        return target;
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {
        target.toRegEx(sb);
        sb.append(options);
    }
}
