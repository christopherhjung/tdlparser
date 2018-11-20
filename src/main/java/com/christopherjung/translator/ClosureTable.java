package com.christopherjung.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClosureTable
{
    private Grammar grammar;
    private List<Kernel> kernels;

    public ClosureTable(Grammar grammar)
    {
        int counter = 0;
        this.grammar = grammar;

        Kernel root = new Kernel(counter++, new BasicItem(0, grammar.getRootRule()), grammar);

        List<HashMap<String, Set<BasicItem>>> closures = new ArrayList<>();
        List<Kernel> kernels = new ArrayList<>();
        HashMap<Integer, HashMap<String, Set<BasicItem>>> map = new HashMap<>();

        kernels.add(root);
        var rootClosure = root.getClosure();
        closures.add(rootClosure);
        map.put(0, rootClosure);

        for (int i = 0; i < closures.size(); i++)
        {
            HashMap<String, Set<BasicItem>> closure = closures.get(i);
            for (String key : closure.keySet())
            {
                Kernel kernel = new Kernel(counter, closure.get(key), grammar);

                if (kernels.contains(kernel))
                {
                    continue;
                }

                kernels.add(kernel);

                var temp = kernel.getClosure();

                map.put(counter, temp);

                //System.out.println(kernel);
                if (temp.size() > 0)
                {
                    closures.add(temp);
                }

                counter++;
            }
        }


        HashMap<Integer, HashMap<String, Integer>> map2 = new HashMap<>();

        for (Integer kernelIndex : map.keySet())
        {
            HashMap<String, Set<BasicItem>> items = map.get(kernelIndex);
            HashMap<String, Integer> test = new HashMap<>();
            map2.put(kernelIndex, test);

            for (String str : items.keySet())
            {
                Set<BasicItem> set = items.get(str);

                Kernel temp = new Kernel(0, set, grammar);

                int index = kernels.indexOf(temp);

                test.put(str, index);
            }
        }

        System.out.println("######################");
        for (var test : map2.entrySet())
        {
            System.out.println(test);
        }

        System.out.println("######################");
        for (var test : map.entrySet())
        {
            System.out.println(test);
        }

        kernels = new ArrayList<>();
        kernels.add(root);
    }
}
