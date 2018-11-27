package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;

import java.util.*;

public class ClosureTable
{
    private Grammar grammar;
    private List<Kernel> kernels;
    private HashMap<Set<BasicItem>, Kernel> kernelHashMap;
    private ParserTable table;

    public ClosureTable(Grammar grammar)
    {
        this.grammar = grammar;
        kernelHashMap = new LinkedHashMap<>();
        kernels = new ArrayList<>();

        Kernel root = new Kernel(new BasicItem(0, grammar.getRootRule()), this);

        kernelHashMap.put(root.getItems(), root);

        List<HashMap<String, Kernel>> closures = new ArrayList<>();
        HashMap<Integer, HashMap<String, Kernel>> map = new HashMap<>();
        kernels.add(root);
        var rootClosure = root.getClosure();
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

                var temp = kernel.getClosure();

                map.put(counter, temp);

                if (temp.size() > 0)
                {
                    closures.add(temp);
                }

                counter++;
            }
        }

        //System.out.println(kernels.stream().map(Object::toString).reduce("",(a,b) -> a+"\n" + b));

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


        table = new ParserTable(grammar);
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

            ParserTable.Entry entry = new ParserTable.Entry(finished,actions, goTos, restore);
            table.addEntry(entry);
        }


    }

    public ParserTable getTable()
    {
        return table;
    }

    public Kernel getOrCreateKernel(Set<BasicItem> items)
    {
        return kernelHashMap.computeIfAbsent(items, ($) -> new Kernel(items, this));
    }

    public Grammar getGrammar()
    {
        return grammar;
    }
}
