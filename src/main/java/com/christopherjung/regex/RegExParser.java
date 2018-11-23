package com.christopherjung.regex;

import com.christopherjung.grammar.*;
import com.christopherjung.parser.Parser;
import com.christopherjung.parser.ParserInputReader;

import java.util.Collection;
import java.util.HashSet;

public class RegExParser extends Parser<TreeNode<Character>>
{
    @Override
    protected TreeNode<Character> parse()
    {
        return new ConcatNode<>(parseOr(), new ValueNode<>());
    }

    public TreeNode<Character> parseRaw(String str)
    {
        reset(str);
        return parseOr();
    }

    protected TreeNode<Character> parseOr()
    {
        TreeNode<Character> state = parseConcat();

        if (hasNext())
        {
            if (eat('|'))
            {
                TreeNode<Character> right = parseOr();
                return new OrNode<>(state, right);
            }
        }

        return state;
    }

    protected TreeNode<Character> parseConcat()
    {
        TreeNode<Character> state = parseMultiplier();

        if (hasNext())
        {
            if (!is('|') && !is('*') && !is(')'))
            {
                return new ConcatNode<>(state, parseConcat());
            }
        }

        return state;
    }

    protected TreeNode<Character> parseMultiplier()
    {
        TreeNode<Character> state = parseParenthesis();

        if (hasNext())
        {
            if (eat('*'))
            {
                return new StarNode<>(state);
            }
            else if (eat('?'))
            {
                return new QuestNode<>(state);
            }
            else if (eat('+'))
            {
                return new PlusNode<>(state);
            }
            else if (eat('{'))
            {
                int from = readInt();
                int to = eat(',') ? readInt() : from;

                if (!eat('}'))
                {
                    throw new ParserInputReader.ParseException("no closing");
                }

                TreeNode<Character> temp = state;
                for (int i = 1; i < to; i++)
                {
                    TreeNode<Character> cloned = state;

                    if (i >= from)
                    {
                        cloned = new QuestNode<>(cloned);
                    }

                    temp = new ConcatNode<>(temp, cloned);
                }

                state = temp;
            }
        }

        return state;
    }

    private int readInt()
    {
        char cha;
        int result = 0;
        while (Character.isDigit(cha = get()))
        {
            result = result * 10 + cha - '0';
            next();
        }

        return result;
    }

    protected TreeNode<Character> parseParenthesis()
    {
        while (hasNext())
        {
            if (eat('('))
            {
                TreeNode<Character> temp = parseOr();
                next();
                return temp;
            }
            else if (eat('.'))
            {
                return OrNode.all(rawFromTo(0, 127));
            }
            else if (eat('['))
            {
                boolean negate = eat('^');

                HashSet<Character> nodes = new HashSet<>();

                Character save = null;

                while (true)
                {
                    if (save != null && eat('-'))
                    {
                        fromToChars(nodes, save, eat());
                        save = null;
                    }
                    else
                    {
                        if (save != null)
                        {
                            nodes.add(save);
                        }

                        if (eat(']'))
                        {
                            break;
                        }

                        save = eat();
                    }
                }

                if (negate)
                {
                    HashSet<Character> complement = new HashSet<>();
                    fromToChars(complement, 0, 127);
                    complement.removeAll(nodes);
                    nodes = complement;
                }

                return OrNode.all(nodes);
            }
            else if (eat('\\'))
            {
                if (eat('d'))
                {
                    return OrNode.all(rawFromTo('0', '9'));
                }
                else if (eat('s'))
                {
                    return OrNode.all(ValueNode.rawOf('\n', ' ', '\t', '\r'));
                }
                else if (eat('w'))
                {
                    return OrNode.all(OrNode.all(rawFromTo('a', 'z')), OrNode.all(rawFromTo('A', 'Z')), new ValueNode<>('_'));
                }
                else
                {
                    return new ValueNode<>(eat());
                }
            }
            else if (!is('|') && !is('*') && !is('+'))
            {
                return new ValueNode<>(eat());
            }
        }

        return null;
    }


    public static Collection<TreeNode<Character>> fromTo(int from, int to)
    {
        HashSet<TreeNode<Character>> set = new HashSet<>();
        fromTo(set, from, to);
        return set;
    }

    public static Collection<Character> rawFromTo(int from, int to)
    {
        HashSet<Character> set = new HashSet<>();
        for (int i = 0; i < to - from - 1; i++)
        {
            set.add((char) (from + i));
        }
        return set;
    }

    public static void fromTo(Collection<TreeNode<Character>> collection, int from, int to)
    {
        for (int i = 0; i < to - from - 1; i++)
        {
            collection.add(new ValueNode<>((char) (from + i)));
        }
    }

    public static void fromToChars(Collection<Character> collection, int from, int to)
    {
        for (int i = 0; i < to - from - 1; i++)
        {
            collection.add((char) (from + i));
        }
    }

    public static Collection<TreeNode<Character>> map(char... chars)
    {
        HashSet<TreeNode<Character>> result = new HashSet<>();
        for (int i = 0; i < chars.length; i++)
        {
            result.add(new ValueNode<>(chars[i]));
        }

        return result;
    }

}
