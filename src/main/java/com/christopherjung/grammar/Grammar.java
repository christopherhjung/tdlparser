package com.christopherjung.grammar;

import com.christopherjung.parser.ParserInputReader;
import com.christopherjung.scanner.Token;
import com.christopherjung.translator.Rule;

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

    public Set<Rule> getChildRules(String name)
    {
        return rules.get(name);
    }

    public boolean contains(String name)
    {
        return rules.containsKey(name);
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (var entry : rules.entrySet())
        {
            for (var rule : entry.getValue())
            {
                if (sb.length() > 0)
                {
                    sb.append('\n');
                }

                sb.append(rule);
            }
        }

        return sb.toString();
    }

    public static class Builder
    {
        private HashMap<String, Set<Rule>> rules;
        private HashSet<String> alphabet;

        private Rule root;
        private int index;

        public Builder()
        {
            rules = new HashMap<>();
            alphabet = new HashSet<>();
            index = 1;
        }

        public Rule setRootRule(String root)
        {
            return this.root = new Rule(0, "__start__", root.split("\\s+"));
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
                String name = inputReader.fetchOver("->").trim();
                String rule = inputReader.fetchLine();

                if (!hasRoot)
                {
                    hasRoot = true;
                    setRootRule(name);
                }

                addRule(name, rule);
                inputReader.eatWhitespace();
            }
        }

        public Rule addRule(String rule)
        {
            String[] parts = rule.split("->");
            return addRule(parts[0].trim(), parts[1].trim());
        }

        public Rule addRule(String name, String ruleDescriptor)
        {
            ruleDescriptor = ruleDescriptor.trim();

            if (ruleDescriptor.isEmpty())
            {
                throw new RuntimeException("No Rule for " + name);
            }

            String[] symbols;

            if (ruleDescriptor.contains("''"))
            {
                symbols = new String[0];
            }
            else
            {
                symbols = ruleDescriptor.split("\\s+");
            }

            Set<String> symbolSet = new HashSet<>(List.of(symbols));
            alphabet.addAll(symbolSet);

            Rule rule = new Rule(index++, name, symbols);
            rules.computeIfAbsent(name, ($) -> new HashSet<>()).add(rule);

            alphabet.removeAll(rules.keySet());

            return rule;
        }

        public Grammar build()
        {
            alphabet.add(Token.EOF.getName());
            return new Grammar(rules, alphabet, root);
        }

        public void removeEpsilon()
        {
            for (String ruleName : rules.keySet())
            {
                Set<Rule> children = rules.get(ruleName);


            }
        }
    }

}
