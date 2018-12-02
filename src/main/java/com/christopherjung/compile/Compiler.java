package com.christopherjung.compile;

import com.christopherjung.container.OrNode;
import com.christopherjung.container.TreeNode;
import com.christopherjung.regex.State;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Compiler
{
    public static class Result
    {

    }

    public static class Builder
    {
        HashMap<String, TreeNode<String>> rules = new HashMap<>();

        public void addRootRule(String rule)
        {
            addRule("start", rule);
        }

        public void addRule(String name, String regEx, Function<Compiler, Object> mapper)
        {

        }

        public void addRule(String name, String rule)
        {
            RuleParser parser = new RuleParser();
            TreeNode<String> parsedRule = parser.parseRaw(rule);

            if (rules.containsKey(name))
            {
                TreeNode<String> other = rules.get(name);

                parsedRule = new OrNode<>(other, parsedRule);
            }

            rules.put(name, parsedRule);
        }

        public Compiler build()
        {
            HashMap<String, State<String>> compiled = new HashMap<>();

            for (String name : rules.keySet())
            {
                TreeNode<String> treeNode = rules.get(name);
                treeNode = TreeNode.close(treeNode);
                State<String> compilerState = State.compile(treeNode);
                compiled.put(name, compilerState);
            }

            return new Compiler(compiled);
        }
    }

    HashMap<String, State<String>> rules;
    private ScanResult scanResult;


    public Compiler(HashMap<String, State<String>> rules)
    {
        this.rules = rules;
    }

    public void addRootRule(String rule)
    {
        addRule("start", rule);
    }

    public void addRule(String name, String rule)
    {
        RuleParser parser = new RuleParser();
        TreeNode<String> parsedRule = parser.parse(rule);
        rules.put(name, State.compile(parsedRule));
    }

    public void compile(ScanResult scanResult)
    {
        this.scanResult = scanResult;

        Rule rule = new Rule(0, rules.get("start"));

        rule.propagate();
    }

    private int position = 0;

    private class Rule
    {
        private State<String> state;
        private int position;

        public Rule(int position, State<String> state)
        {
            this.state = state;
            this.position = position;
        }

        public int propagate()
        {
            List<Rule> rulesChain = new ArrayList<>();
            int currentPosition = position;
            while (currentPosition < scanResult.size())
            {
                Token left = scanResult.get(currentPosition);
                String token = left.getName();

                boolean fail = false;
                if (rules.containsKey(token))
                {
                    Rule rule = new Rule(position, rules.get(token));
                    int nextPosition = rule.propagate();

                    if (nextPosition == -1)
                    {
                        fail = true;
                    }
                    else
                    {
                        currentPosition = nextPosition;
                        rulesChain.add(rule);
                    }

                }
                else
                {
                    state = state.propagate(token);

                    if (state == null)
                    {
                        fail = true;
                    }
                    else
                    {
                        currentPosition++;

                        if (state.isAccept())
                        {
                            return currentPosition;
                        }
                    }
                }

                while (fail)
                {
                    if (rulesChain.size() == 0)
                    {
                        return -1;
                    }

                    Rule lastRule = rulesChain.get(rulesChain.size() - 1);
                    int nextPosition = lastRule.propagate();

                    if (nextPosition == -1)
                    {
                        rulesChain.remove(rulesChain.size() - 1);
                    }
                    else
                    {
                        fail = false;
                        currentPosition = nextPosition;
                    }
                }
            }

            return -1;
        }
    }

    /*public boolean fetch(int position, State<String> state)
    {
        while (position < tokens.size())
        {
            Scanner.ScanResult left = tokens.get(position);
            String token = left.getName();

            if (rules.containsKey(token))
            {
                if (!fetch(position, rules.get(token)))
                {
                    return false;
                }
            }
            else
            {
                state = state.propagate(token);
                position++;
            }

            if (state == null)
            {
                return position == tokens.size();
            }
            else if (state.isAccept())
            {
                if (fetch(position, rules.get(token)))
                {
                    return true;
                }
            }
        }

        return false;
    }*/


}
