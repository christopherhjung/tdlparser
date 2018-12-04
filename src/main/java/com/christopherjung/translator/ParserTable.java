package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.scanner.Token;

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

    public Entry getEntry(int key)
    {
        return entries.get(key);
    }

    public boolean isIgnore(String rule)
    {
        return ignores.contains(rule);
    }

    public static class Entry
    {
        private HashMap<String, Integer> actions;
        private HashMap<String, Integer> goTos;
        private int restoreActions;
        private Rule rule;
        private Kernel kernel;

        public Entry(Rule rule, HashMap<String, Integer> actions, HashMap<String, Integer> goTos, int restoreActions, Kernel kernel)
        {
            this.rule = rule;
            this.actions = actions;
            this.goTos = goTos;
            this.restoreActions = restoreActions;
            this.kernel = kernel;
        }

        public Kernel getKernel()
        {
            return kernel;
        }

        public HashMap<String, Integer> getActions()
        {
            return actions;
        }

        public Integer getAction(Token token)
        {
            return actions.get(token.getName());
        }

        public HashMap<String, Integer> getGoTos()
        {
            return goTos;
        }

        public Integer getGoTo(Token token)
        {
            return goTos.get(token.getName());
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
