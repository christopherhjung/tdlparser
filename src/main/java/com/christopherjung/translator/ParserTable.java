package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ParserTable
{
    private Grammar grammar;
    private ArrayList<Entry> entries;
    private Set<String> ignores;

    public ParserTable(Grammar grammar, Set<String> ignores)
    {
        this.ignores = ignores;
        this.grammar = grammar;
        this.entries = new ArrayList<>();
    }

    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }

    public Grammar getGrammar()
    {
        return grammar;
    }

    public ArrayList<Entry> getEntries()
    {
        return entries;
    }

    public boolean isIgnore(String rule)
    {
        return ignores.contains(rule);
    }

    public static class Entry
    {
        private HashMap<String, Integer> actions;
        private HashMap<String, Integer> goTos;
        private Set<String> ignores;
        private int restoreActions;
        private Rule rule;

        public Entry(Rule rule, HashMap<String, Integer> actions, HashMap<String, Integer> goTos, int restoreActions)
        {
            this.rule = rule;
            this.actions = actions;
            this.goTos = goTos;
            this.restoreActions = restoreActions;
            this.ignores = ignores;
        }

        public HashMap<String, Integer> getActions()
        {
            return actions;
        }

        public HashMap<String, Integer> getGoTos()
        {
            return goTos;
        }

        public int getRestoreAction()
        {
            return restoreActions;
        }

        public boolean hasRestoreRule()
        {
            return restoreActions >= 0;
        }

        public Rule getRule()
        {
            return rule;
        }

        @Override
        public String toString()
        {
            return goTos.toString();
        }
    }
}
