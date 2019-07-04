package com.christopherjung.regex;

import com.christopherjung.container.*;

import java.util.*;

public class NDA<T>
{
    private int position = 0;

    private Map<Integer, T> values = new HashMap<>();
    private Map<Integer, Boolean> nullifies = new HashMap<>();

    private List<Set<Integer>> firstPositions = new ArrayList<>();
    private List<Set<Integer>> lastPositions = new ArrayList<>();
    private Map<Integer, Set<Integer>> followPositions = new HashMap<>();
    private Set<Integer> finish = new HashSet<>();
    private Set<Integer> lookaheads = new HashSet<>();

    public T getValue(int key)
    {
        return values.get(key);
    }

    public boolean isLookahead(int key)
    {
        return lookaheads.contains(key);
    }

    public Set<Integer> getFirstPositions()
    {
        return firstPositions.get(0);
    }

    public Set<Integer> getFollowPositions(int key)
    {
        return followPositions.get(key);
    }

    public boolean isFinish(int key)
    {
        return finish.contains(key);
    }

    public static <T> NDA<T> create(TreeNode<T> root)
    {
        NDA<T> nda = new NDA<>();
        nda.compute(root);


        return nda;
    }

    private void compute(TreeNode<T> root)
    {
        int last = computeRecursive(root);

        if (nullifies.get(last))
        {
            firstPositions.get(last).add(position);
        }

        for (int lastPosition : lastPositions.get(last))
        {
            followPositions.get(lastPosition).add(position);
        }

        if (finish.size() == 0)
        {
            finish.add(position);
        }

    }

    private int computeRecursive(TreeNode<T> root)
    {
        int index = position++;

        Set<Integer> thisFirstPositions = new HashSet<>();
        Set<Integer> thisLastPositions = new HashSet<>();
        firstPositions.add(thisFirstPositions);
        lastPositions.add(thisLastPositions);

        if (root instanceof BinaryNode)
        {
            BinaryNode<T> binaryNode = (BinaryNode<T>) root;

            int leftIndex = computeRecursive(binaryNode.getLeft());
            int rightIndex = computeRecursive(binaryNode.getRight());

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
            int valueIndex = computeRecursive(unaryNode.getValue());

            thisFirstPositions.addAll(firstPositions.get(valueIndex));
            thisLastPositions.addAll(lastPositions.get(valueIndex));

            if (root instanceof LookaheadNode)
            {
                nullifies.put(index, false);
                for (int i = valueIndex; i <= position; i++)
                {
                    lookaheads.add(i);
                }
            }
            else if (root instanceof NegativeLookaheadNode)
            {
                throw new RuntimeException("not yet implemented");
            }
            else
            {
                nullifies.put(index, !(root instanceof PlusNode));

                if (!(root instanceof QuestNode))
                {
                    for (int child : lastPositions.get(valueIndex))
                    {
                        followPositions.get(child).addAll(thisFirstPositions);
                    }
                }
            }
        }
        else if (root instanceof ValueNode)
        {
            ValueNode<T> value = (ValueNode<T>) root;
            followPositions.put(index, new HashSet<>());

            nullifies.put(index, false);
            values.put(index, value.getValue());

            thisFirstPositions.add(index);
            thisLastPositions.add(index);
        }

        return index;
    }
}
