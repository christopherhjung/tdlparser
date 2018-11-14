package com.christopherjung.parser;

import java.util.HashMap;

public class RegExStringifier
{
    private StringBuilder builder = new StringBuilder();

    private static HashMap<Class<? extends TreeNode>,Integer> order = new HashMap<>();

    static{
        order.put(OrNode.class,0);
        order.put(ConcatNode.class,1);
        order.put(PlusNode.class,2);
        order.put(QuestNode.class,2);
        order.put(StarNode.class,2);
    }

    public void visit(TreeNode node)
    {
        /*
        try
        {
            getClass().getMethod("visit", getClass()).invoke(this, node);
        } catch (Exception e)
        {

        }*/
    }

    @Override
    public String toString()
    {
        return builder.toString();
    }
}
