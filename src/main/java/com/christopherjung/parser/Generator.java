package com.christopherjung.parser;

import com.christopherjung.grammar.Grammar;

import java.util.*;


public class Generator
{
    private Grammar grammar;
    private HashMap<Set<BasicItem>, Kernel> kernelHashMap;
    private List<Kernel> kernelList = new ArrayList<>();
    private HashMap<Kernel, Integer> kernels = new HashMap<>();
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

        kernels = new HashMap<>();
        Kernel root = new Kernel(new BasicItem(0, grammar.getRoot(), rootLookahead));
        kernels.put(root, kernels.size());
        kernelList.add(root);

        kernelHashMap = new LinkedHashMap<>();
        kernelHashMap.put(root.getItems(), root);

        HashMap<Integer, HashMap<String, Kernel>> targetClosures = new HashMap<>();
        targetClosures.put(0, createClosure(root));

        System.out.println("Generate Closure");

        for (int i = 0; i < targetClosures.size(); i++)
        {
            HashMap<String, Kernel> closure = targetClosures.get(i);
            for (String key : closure.keySet())
            {
                Kernel kernel = closure.get(key);

                if (kernels.containsKey(kernel))
                {
                    continue;
                }

                targetClosures.put(kernels.size(), createClosure(kernel));
                kernels.put(kernel, kernels.size());
                kernelList.add(kernel);
            }
        }


        System.out.println("Generate Parsing Table");
        ParserTable table = new ParserTable(grammar, ignores);
        for (Integer kernelIndex : targetClosures.keySet())
        {
            HashMap<String, Kernel> items = targetClosures.get(kernelIndex);

            HashMap<String, Integer> goTos = new HashMap<>();
            HashMap<String, Integer> actions = new HashMap<>();
            HashMap<String, Rule> restoreRules = new HashMap<>();

            for (String str : items.keySet())
            {
                Kernel kernel = items.get(str);

                int index = kernels.get(kernel);

                goTos.put(str, index);
            }

            Set<BasicItem> finished = kernelList.get(kernelIndex).getFinishedItems();

            for (BasicItem item : finished)
            {
                for (String look : item.getLookahead())
                {
                    if (restoreRules.containsKey(look))
                    {
                        throw new RuntimeException("multiple rules! " + look);
                    }

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

            table.addEntry(new ParserTable.Entry(actions, goTos, restoreRules));
        }

        return table;
    }

    private Set<String> estimateLookahead(BasicItem item)
    {
        if (item.isFinished())
        {
            return item.getLookahead();
        }
        else
        {
            if (grammar.getAlphabet().contains(item.getNextKey()))
            {
                return Set.of(item.getNextKey());
            }
            else
            {
                return getFirst(item.getNextKey());
            }
        }
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
            BasicItem nextItem = item.next();

            result.computeIfAbsent(key, ($) -> new HashSet<>()).add(nextItem);

            if (grammar.contains(key))
            {
                Set<String> lookahead = estimateLookahead(nextItem);

                for (Rule rule : grammar.getChildRules(key))
                {
                    BasicItem newItem = new BasicItem(0, rule, lookahead);
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
            LinkedList<BasicItem> merged = new LinkedList<>(result.get(symbol));
            Set<BasicItem> after = new HashSet<>();

            for (; merged.size() > 0; )
            {
                ListIterator<BasicItem> listIterator = merged.listIterator();
                BasicItem first = listIterator.next();
                listIterator.remove();
                Set<String> lookahead = null;
                for (; listIterator.hasNext(); )
                {
                    BasicItem other = listIterator.next();

                    if (first.getDotIndex() == other.getDotIndex() && first.getRule().equals(other.getRule()))
                    {
                        if (lookahead == null)
                        {
                            lookahead = new HashSet<>(first.getLookahead());
                        }

                        lookahead.addAll(other.getLookahead());
                        listIterator.remove();
                    }
                }

                if (lookahead != null)
                {
                    first = new BasicItem(first.getDotIndex(), first.getRule(), lookahead);
                }

                after.add(first);
            }

            if (transform.containsKey(symbol))
            {
                throw new RuntimeException("hääää");
            }

            if (kernelHashMap.containsKey(after))
            {
                transform.put(symbol, kernelHashMap.get(after));
            }
            else
            {
                checkSingleFinish(symbol, after);

                Kernel newOne = new Kernel(after);
                kernelHashMap.put(after, newOne);
                transform.put(symbol, newOne);
            }
        }

        return transform;
    }

    private void checkSingleFinish(String symbol, Collection<BasicItem> items)
    {
        HashMap<String, List<BasicItem>> lookahead = new HashMap<>();
        HashMap<String, List<BasicItem>> result = new HashMap<>();
        boolean found = false;

        for (BasicItem item : items)
        {
            if (item.isFinished())
            {
                for (String ahead : item.getLookahead())
                {
                    lookahead.computeIfAbsent(ahead, ($) -> new ArrayList<>()).add(item);

                    if (lookahead.get(ahead).size() == 2)
                    {
                        found = true;
                        result.put(ahead, lookahead.get(ahead));
                    }
                }
            }
        }

        if (found)
        {
            System.out.println(symbol + " " + result);
        }
    }
}
