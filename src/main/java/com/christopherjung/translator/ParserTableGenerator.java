package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;

import java.util.*;

public class ParserTableGenerator
{
    private Grammar grammar;
    private HashMap<Set<BasicItem>, Kernel> kernelHashMap;
    private List<Kernel> kernels = new ArrayList<>();

    public ParserTable generate(Grammar grammar)
    {
        return generate(grammar, new HashSet<>());
    }

    public ParserTable generate(Grammar grammar, Set<String> ignores)
    {
        this.grammar = grammar;

        Kernel root = new Kernel(new BasicItem(0, grammar.getRootRule()));

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

            Rule finished = kernels.get(kernelIndex).getFinished();
            int restore = -1;

            if (finished != null)
            {
                restore = finished.getId();
            }

            for (String sign : grammar.getAlphabet())
            {
                if (goTos.containsKey(sign))
                {
                    actions.put(sign, goTos.remove(sign));
                }
            }

            table.addEntry(new ParserTable.Entry(finished, actions, goTos, restore, kernels.get(kernelIndex)));
        }

        return table;
    }

    public HashMap<String, Kernel> createClosure(Kernel kernel)
    {
        System.out.println(kernel);

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
                for (Rule rule : grammar.getChildRules(key))
                {
                    BasicItem newItem = new BasicItem(0, rule);
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

        for (BasicItem item : items)
        {
            if (item.isFinished())
            {
                last.add(item);
            }
        }

        if (last.size() > 1)
        {
            throw new RuntimeException("Multiple FinishRules  : " + last);
        }
    }

    public Kernel getOrCreateKernel(Set<BasicItem> items)
    {
        return kernelHashMap.computeIfAbsent(items, ($) -> new Kernel(items));
    }
}
