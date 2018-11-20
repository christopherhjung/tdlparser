package com.christopherjung.translator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

        public Builder()
        {
            rules = new HashMap<>();
            alphabet = new HashSet<>();
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

        public void addRule(String rule)
        {
            String[] parts = rule.split("->");
            addRule(parts[0].trim(), parts[1].trim());
        }

        public void addRule(String name, String ruleDescriptor)
        {
            System.out.println(Arrays.toString(ruleDescriptor.trim().split("\\s+")));
            Rule rule = new Rule(name, ruleDescriptor.trim().split("\\s+"));
            rules.computeIfAbsent(name, ($) -> new HashSet<>()).add(rule);
        }

        public Grammar build()
        {
            return new Grammar(rules, alphabet, new Rule("__start__", new String[]{root}));
        }
    }

}
