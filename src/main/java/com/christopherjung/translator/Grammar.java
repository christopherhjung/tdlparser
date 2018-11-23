package com.christopherjung.translator;

import com.christopherjung.parser.ParserInputReader;

import java.io.InputStream;
import java.util.*;

public class Grammar
{
    private HashMap<String, Set<Rule>> rules;
    private HashSet<String> alphabet;
    private Rule root;

    public Grammar(HashMap<String, Set<Rule>> rules, HashSet<String> alphabet, Rule root)
    {
        this.rules = new HashMap<>(rules);
        this.alphabet = new HashSet<>(alphabet);
        this.root = root;
    }

    public Set<String> getAlphabet()
    {
        return Collections.unmodifiableSet(alphabet);
    }

    public Set<String> getRuleNames()
    {
        return rules.keySet();
    }

    public Rule getRootRule()
    {
        return root;
    }

    public Set<Rule> getRule(String name)
    {
        return rules.get(name);
    }

    public boolean contains(String name)
    {
        return rules.containsKey(name);
    }

    public static class Builder
    {
        private HashMap<String, Set<Rule>> rules;
        private HashSet<String> alphabet;

        private String root;
        private int index;

        public Builder()
        {
            rules = new HashMap<>();
            alphabet = new HashSet<>();
            index = 1;
        }

        public void setRootRule(String root)
        {
            if (root.contains(" "))
            {
                throw new RuntimeException("Runtime");
            }

            this.root = root;
        }

        public void addRules(String rules)
        {
            for (String ruleDescription : rules.split("\n"))
            {
                addRule(ruleDescription);
            }
        }

        public void addRules(InputStream inputStream)
        {
            var inputReader = new ParserInputReader(inputStream);
            inputReader.eatWhitespace();
            boolean hasRoot = false;
            while (inputReader.hasNext())
            {
                String name = inputReader.fetchUntil("->").trim();
                inputReader.next("->".length());
                String rule = inputReader.fetchUntil("\n");

                if (!hasRoot)
                {
                    hasRoot = true;
                    setRootRule(name);
                }

                addRule(name, rule);
                inputReader.eatWhitespace();
            }

        }

        public void addRule(String rule)
        {
            String[] parts = rule.split("->");
            addRule(parts[0].trim(), parts[1].trim());
        }

        public void addRule(String name, String ruleDescriptor)
        {
            String[] symbols = ruleDescriptor.trim().split("\\s+");

            Set<String> symbolSet = new HashSet<>(Set.of(symbols));
            alphabet.addAll(symbolSet);

            Rule rule = new Rule(index++, name, symbols);
            rules.computeIfAbsent(name, ($) -> new HashSet<>()).add(rule);

            alphabet.removeAll(rules.keySet());
        }

        public Grammar build()
        {
            return new Grammar(rules, alphabet, new Rule(0, "__start__", new String[]{root}));
        }
    }

}
