package com.christopherjung.nda;

import com.christopherjung.grammar.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

public class NDA<T>
{
    private Supplier<Collection<Integer>> supplier = HashSet::new;
    private Supplier<Map<Integer, Collection<Integer>>> mapSupplier = HashMap::new;

    private int position = 0;

    private Map<Integer, T> values = new HashMap<>();
    private Map<Integer, Boolean> nullifies = new HashMap<>();

    private Map<Integer, Collection<Integer>> firstPositions = mapSupplier.get();
    private Map<Integer, Collection<Integer>> lastPositions = mapSupplier.get();
    private Map<Integer, Collection<Integer>> followPositions = mapSupplier.get();

    public T getValues(int key)
    {
        return values.get(key);
    }

    public Collection<Integer> getFirstPositions()
    {
        return firstPositions.get(0);
    }

    public Collection<Integer> getFollowPositions(int key)
    {
        return followPositions.get(key);
    }

    public void from(TreeNode<T> root)
    {
        compute(root);

/*
        System.out.println(values);
        System.out.println(firstPositions);
        System.out.println(followPositions);

        System.out.println("-----------------");*/
    }

    public int compute(TreeNode<T> root)
    {
        int index = position++;

        Collection<Integer> thisFirstPositions = supplier.get();
        Collection<Integer> thisLastPositions = supplier.get();
        firstPositions.put(index, thisFirstPositions);
        lastPositions.put(index, thisLastPositions);

        if (root instanceof BinaryNode)
        {
            BinaryNode<T> binaryNode = (BinaryNode<T>) root;

            TreeNode<T> left = binaryNode.getLeft();
            TreeNode<T> right = binaryNode.getRight();

            int leftIndex = compute(left);
            int rightIndex = compute(right);

            if (root instanceof ConcatNode)
            {
                nullifies.put(index, nullifies.get(leftIndex) && nullifies.get(rightIndex));

                thisFirstPositions.addAll(firstPositions.get(leftIndex));
                if (nullifies.get(leftIndex))
                {
                    thisFirstPositions.addAll(firstPositions.get(rightIndex));
                }

                if (nullifies.get(rightIndex))
                {
                    thisLastPositions.addAll(lastPositions.get(leftIndex));
                }
                thisLastPositions.addAll(lastPositions.get(rightIndex));

                for (int lastPosition : lastPositions.get(leftIndex))
                {
                    followPositions.get(lastPosition).addAll(firstPositions.get(rightIndex));
                }
            }
            else if (root instanceof OrNode)
            {
                nullifies.put(index, nullifies.get(leftIndex) || nullifies.get(rightIndex));

                thisFirstPositions.addAll(firstPositions.get(leftIndex));
                thisFirstPositions.addAll(firstPositions.get(rightIndex));

                thisLastPositions.addAll(lastPositions.get(leftIndex));
                thisLastPositions.addAll(lastPositions.get(rightIndex));
            }
        }
        else if (root instanceof UnaryNode)
        {
            UnaryNode<T> unaryNode = (UnaryNode<T>) root;

            TreeNode<T> node = unaryNode.getValue();

            int valueIndex = compute(node);

            thisFirstPositions.addAll(firstPositions.get(valueIndex));
            thisLastPositions.addAll(lastPositions.get(valueIndex));

            if (root instanceof PlusNode)
            {
                nullifies.put(index, false);

                for (int child : lastPositions.get(valueIndex))
                {
                    followPositions.get(child).addAll(thisFirstPositions);
                }
            }
            else if (root instanceof QuestNode)
            {
                nullifies.put(index, true);
            }
            else if (root instanceof StarNode)
            {
                nullifies.put(index, true);

                for (int child : lastPositions.get(valueIndex))
                {
                    followPositions.get(child).addAll(thisFirstPositions);
                }
            }
        }
        else if (root instanceof ValueNode)
        {
            ValueNode<T> value = (ValueNode<T>) root;
            followPositions.put(index, supplier.get());

            nullifies.put(index, false);
            values.put(index, value.getValue());

            thisFirstPositions.add(index);
            thisLastPositions.add(index);
        }

        return index;
    }
}
