package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;

import java.util.*;

public class ParserTableGenerator
{
    private Grammar grammar;
    private HashMap<Set<BasicItem>, Kernel> kernelHashMap;

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

        List<Kernel> kernels = new ArrayList<>();
        kernels.add(root);

        var rootClosure = createClosure(root);
        List<HashMap<String, Kernel>> closures = new ArrayList<>();
        HashMap<Integer, HashMap<String, Kernel>> map = new HashMap<>();
        closures.add(rootClosure);
        map.put(0, rootClosure);


        int counter = 1;
        for (int i = 0; i < closures.size(); i++)
        {
            HashMap<String, Kernel> closure = closures.get(i);
            for (String key : closure.keySet())
            {
                Kernel kernel = closure.get(key);

                if (kernels.contains(kernel))
                {
                    continue;
                }

                kernels.add(kernel);

                var childClosures = createClosure(kernel);

                map.put(counter, childClosures);

                if (childClosures.size() > 0)
                {
                    closures.add(childClosures);
                }

                counter++;
            }
        }

        HashMap<Integer, HashMap<String, Integer>> map2 = new HashMap<>();

        for (Integer kernelIndex : map.keySet())
        {
            HashMap<String, Kernel> items = map.get(kernelIndex);
            HashMap<String, Integer> test = new HashMap<>();
            map2.put(kernelIndex, test);

            for (String str : items.keySet())
            {
                Kernel temp = items.get(str);

                int index = kernels.indexOf(temp);

                test.put(str, index);
            }
        }

        ParserTable table = new ParserTable(grammar, ignores);
        for (int layer : map2.keySet())
        {
            HashMap<String, Integer> goTos = map2.get(layer);
            HashMap<String, Integer> actions = new HashMap<>();

            Rule finished = kernels.get(layer).getFinished();
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

            table.addEntry(new ParserTable.Entry(finished, actions, goTos, restore));
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
            transform.put(symbol, getOrCreateKernel(result.get(symbol)));
        }

        return transform;
    }

    public Kernel getOrCreateKernel(Set<BasicItem> items)
    {
        return kernelHashMap.computeIfAbsent(items, ($) -> new Kernel(items));
    }
}
