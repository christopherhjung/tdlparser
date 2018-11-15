package com.christopherjung.compile;

import com.christopherjung.regex.TreeNode;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Compiler
{
    HashMap<String, RuleState<String>> rules = new HashMap<>();
    private ScanResult scanResult;

    public void addRule(String name, String rule)
    {
        RuleParser parser = new RuleParser();
        TreeNode<String> parsedRule = parser.parse(rule);
        rules.put(name, RuleState.compile(parsedRule));
    }

    public void compile(ScanResult scanResult)
    {
        this.scanResult = scanResult;

    }

    private int position = 0;

    /*
    private class Rule
    {
        private RuleState<String> state;
        private int position;

        public Rule(int position, RuleState<String> state)
        {
            this.state = state;
            this.position = position;
        }

        public int propagate()
        {
            List<Rule> rulesChain = new ArrayList<>();
            int currentPosition = position;
            while (currentPosition < tokens.size())
            {
                Scanner.ScanResult left = tokens.get(currentPosition);
                String token = left.getToken();

                System.out.println(position + " " + token);
                System.out.println(rules);

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
                    System.out.println(position + " " + token + " " + state);
                    state = state.propagate(token);
                    System.out.println(state);

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
    }*/

    /*public boolean test(int position, State<String> state)
    {
        while (position < tokens.size())
        {
            Scanner.ScanResult left = tokens.get(position);
            String token = left.getToken();

            if (rules.containsKey(token))
            {
                if (!test(position, rules.get(token)))
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
                if (test(position, rules.get(token)))
                {
                    return true;
                }
            }
        }

        return false;
    }*/


}
