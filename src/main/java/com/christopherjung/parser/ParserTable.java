package com.christopherjung.parser;

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
        private HashMap<String, Rule> restore;

        public Entry(HashMap<String, Integer> actions, HashMap<String, Integer> goTos, HashMap<String,Rule> restore)
        {
            this.actions = actions;
            this.goTos = goTos;
            this.restore = restore;
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

        public Rule getRestoreRule(Token token)
        {
            return restore.get(token.getName());
        }

        public HashMap<String, Rule> getRestoreRules()
        {
            return restore;
        }

        @Override
        public String toString()
        {
            return actions.toString() + " " + goTos.toString();
        }
    }
}
