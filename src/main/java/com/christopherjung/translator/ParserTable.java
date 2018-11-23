package com.christopherjung.translator;

import java.util.ArrayList;
import java.util.HashMap;

public class ParserTable
{
    private Grammar grammar;
    private ArrayList<Entry> entries;

    public ParserTable(Grammar grammar)
    {
        this.grammar = grammar;
        this.entries = new ArrayList<>();
    }

    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }

    public ArrayList<Entry> getEntries()
    {
        return entries;
    }

    public static class Entry
    {
        private HashMap<String, Integer> actions;
        private HashMap<String, Integer> goTos;
        private int restoreActions;
        private Rule rule;

        public Entry(Rule rule, HashMap<String, Integer> actions, HashMap<String, Integer> goTos, int restoreActions)
        {
            this.rule = rule;
            this.actions = actions;
            this.goTos = goTos;
            this.restoreActions = restoreActions;
        }

        public HashMap<String, Integer> getActions()
        {
            return actions;
        }

        public HashMap<String, Integer> getGoTos()
        {
            return goTos;
        }

        public int getRestoreActions()
        {
            return restoreActions;
        }

        public Rule getRule()
        {
            return rule;
        }
    }
}
