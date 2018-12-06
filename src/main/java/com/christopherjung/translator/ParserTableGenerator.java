package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;

import java.util.*;

public class ParserTableGenerator
{
    private Grammar grammar;
    private HashMap<Set<BasicItem>, Kernel> kernelHashMap;
    private List<Kernel> kernels = new ArrayList<>();
    private HashMap<String, Set<String>> firsts = new HashMap<>();

    public ParserTable generate(Grammar grammar)
    {
        return generate(grammar, new HashSet<>());
    }


    public Set<String> getFirst(String ruleName)
    {
        if (firsts.containsKey(ruleName))
        {
            return firsts.get(ruleName);
        }

        Set<Rule> children = grammar.getChildRules(ruleName);
        Set<String> first = new HashSet<>();
        firsts.put(ruleName, first);

        for (Rule rule : children)
        {
            if (rule.size() > 0)
            {
                String key = rule.getKey(0);
                if (grammar.getAlphabet().contains(key))
                {
                    first.add(key);
                }
                else
                {
                    first.addAll(getFirst(key));
                }
            }
            else
            {
                throw new RuntimeException("Nullable Rules not supported yet");
            }
        }

        return first;
    }

    public ParserTable generate(Grammar grammar, Set<String> ignores)
    {
        this.grammar = grammar;

        HashSet<String> rootLookahead = new HashSet<>();
        rootLookahead.add("EOF");
        Kernel root = new Kernel(new BasicItem(0, grammar.getRootRule(), rootLookahead));

        kernelHashMap = new LinkedHashMap<>();
        kernelHashMap.put(root.getItems(), root);

        kernels = new ArrayList<>();
        kernels.add(root);

        HashMap<Integer, HashMap<String, Kernel>> targetClosures = new HashMap<>();
        targetClosures.put(0, createClosure(root));

        for (int i = 0; i < targetClosures.size(); i++)
        {
            HashMap<String, Kernel> closure = targetClosures.get(i);
            for (String key : closure.keySet())
            {
                Kernel kernel = closure.get(key);

                if (kernels.contains(kernel))
                {
                    continue;
                }

                targetClosures.put(kernels.size(), createClosure(kernel));
                kernels.add(kernel);
            }
        }

        ParserTable table = new ParserTable(grammar, ignores);
        for (Integer kernelIndex : targetClosures.keySet())
        {
            HashMap<String, Kernel> items = targetClosures.get(kernelIndex);

            HashMap<String, Integer> goTos = new HashMap<>();
            HashMap<String, Integer> actions = new HashMap<>();

            for (String str : items.keySet())
            {
                Kernel kernel = items.get(str);

                int index = kernels.indexOf(kernel);

                goTos.put(str, index);
            }

            Set<BasicItem> finished = kernels.get(kernelIndex).getFinishedItems();
            HashMap<String, Rule> restoreRules = new HashMap<>();
            int restore = -1;

            for (BasicItem item : finished)
            {
                for (String look : item.getLookahead())
                {
                    restoreRules.put(look, item.getRule());
                }
            }

            for (String sign : grammar.getAlphabet())
            {
                if (goTos.containsKey(sign))
                {
                    actions.put(sign, goTos.remove(sign));
                }
            }

            table.addEntry(new ParserTable.Entry( actions, goTos, restoreRules));
        }

        return table;
    }

    public HashMap<String, Kernel> createClosure(Kernel kernel)
    {

        HashMap<String, Set<BasicItem>> result = new LinkedHashMap<>();
        HashSet<BasicItem> visited = new HashSet<>(kernel.getItems());
        LinkedList<BasicItem> current = new LinkedList<>(kernel.getItems());

        for (; !current.isEmpty(); )
        {
            BasicItem item = current.pop();
            if (item.isFinished())
            {
                continue;
            }

            String key = item.getNextKey();


            BasicItem nextItem = new BasicItem(item.getDotIndex() + 1, item.getRule(), item.getLookahead());

            result.computeIfAbsent(key, ($) -> new HashSet<>()).add(nextItem);

            HashSet<String> lookahead = new HashSet<>();

            if (nextItem.isFinished())
            {
                lookahead.addAll(item.getLookahead());
            }
            else
            {
                if (grammar.getAlphabet().contains(nextItem.getNextKey()))
                {
                    lookahead.add(nextItem.getNextKey());
                }
                else
                {
                    lookahead.addAll(getFirst(nextItem.getNextKey()));
                }
            }

            if (grammar.contains(key))
            {
                for (Rule rule : grammar.getChildRules(key))
                {
                    BasicItem newItem = new BasicItem(0, rule, lookahead);
                    //System.out.println(newItem);
                    if (!visited.contains(newItem))
                    {
                        current.push(newItem);
                        visited.add(newItem);
                    }
                }
            }
        }

        HashMap<String, Kernel> transform = new HashMap<>();

        for (String symbol : result.keySet())
        {
            checkSingleFinish(result.get(symbol));

            transform.put(symbol, getOrCreateKernel(result.get(symbol)));
        }

        return transform;
    }

    private void checkSingleFinish(Set<BasicItem> items)
    {
        Set<BasicItem> last = new HashSet<>();
        Set<String> lookahead = new HashSet<>();
        boolean found = false;

        for (BasicItem item : items)
        {
            if (item.isFinished())
            {
                found |= !Collections.disjoint(lookahead, item.getLookahead());
                last.add(item);
                lookahead.addAll(item.getLookahead());
            }
        }

        if (found)
        {
            throw new RuntimeException("Multiple FinishRules  : " + last);
        }
    }

    public Kernel getOrCreateKernel(Set<BasicItem> items)
    {
        return kernelHashMap.computeIfAbsent(items, ($) -> new Kernel(items));
    }
}
