package com.christopherjung.regex;

import com.christopherjung.container.*;
import com.christopherjung.parser.simple.Parser;
import com.christopherjung.parser.simple.ParserInputReader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class RegExParser extends Parser<TreeNode<Character>>
{
    @Override
    protected TreeNode<Character> parse()
    {
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
                    throw new ParserInputReader.ParseException(state + " no closing " + fetch(20));
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
                UnaryOperator<TreeNode<Character>> mapper;
                if (eat('?'))
                {
                    if (eat('='))
                    {
                        mapper = LookaheadNode::new;
                    }
                    else if (eat('!'))
                    {
                        mapper = NegativeLookaheadNode::new;
                    }
                    else
                    {
                        throw new RuntimeException("Optional Parenthesis error!");
                    }
                }
                else
                {
                    mapper = node -> node;
                }

                TreeNode<Character> temp = parseOr();
                if (!eat(')'))
                {
                    throw new RuntimeException("No Parenthesis closing!");
                }
                return mapper.apply(temp);
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
                        if (is('\\'))
                        {
                            throw new ParserInputReader.ParseException("jedslds");
                        }

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

                        if (is('\\'))
                        {
                            nodes.addAll(parseValues());
                        }
                        else
                        {
                            save = eat();
                        }
                    }
                }

                if (negate)
                {
                    nodes = negate(nodes);
                }

                return OrNode.all(nodes);
            }
            else if (!is('|') && !is('*') && !is('+'))
            {
                return parseValue();
            }
        }

        return null;
    }

    private HashSet<Character> negate(Collection<Character> nodes)
    {
        HashSet<Character> complement = new HashSet<>();
        fromToChars(complement, 0, 127);
        complement.removeAll(nodes);

        return complement;
    }

    public TreeNode<Character> parseValue()
    {
        if (is('\\'))
        {
            return OrNode.all(parseValues());
        }
        else if (eat('#'))
        {
            if (!eat('{'))
            {
                throw new RuntimeException("not possible");
            }

            String leftString = "0";
            String rightString = fetchWhile(cha -> cha != ',' && cha != '}');

            if (!is('}'))
            {
                eat(',');
                leftString = rightString;
                rightString = fetchUntil("}");
            }

            long left = Long.parseLong(leftString);
            long right = Long.parseLong(rightString);

            if (!eat('}'))
            {
                throw new RuntimeException("not possible");
            }

            System.out.println("lol");
            TreeNode<Character> treeNode = numberMatch(left, right);

            return treeNode;
        }

        return new ValueNode<>(eat());
    }

    public Collection<Character> specialValues(char cha)
    {
        if (cha == 'n')
        {
            return Set.of('\n');
        }
        else if (cha == 'd')
        {
            return rawFromTo('0', '9');
        }
        else if (cha == 's')
        {
            return Set.of('\n', ' ', '\t', '\r');
        }
        else if (cha == 'w')
        {
            HashSet<Character> set = new HashSet<>();

            set.addAll(rawFromTo('a', 'z'));
            set.addAll(rawFromTo('A', 'Z'));
            set.add('_');

            return set;
        }
        else if (cha == 'W' || cha == 'S' || cha == 'D')
        {
            return negate(specialValues(Character.toLowerCase(cha)));
        }
        else
        {
            return Set.of(cha);
        }
    }

    public Collection<Character> parseValues()
    {
        if (eat('\\'))
        {
            return specialValues(eat());
        }

        return Set.of(eat());
    }

    public static Collection<Character> rawFromTo(int from, int to)
    {
        HashSet<Character> set = new HashSet<>();
        for (int i = to; i >= from; i--)
        {
            set.add((char) i);
        }
        return set;
    }

    public static void fromToChars(Collection<Character> collection, int from, int to)
    {
        for (int i = to; i >= from; i--)
        {
            collection.add((char) i);
        }
    }

    private static TreeNode<Character> DIGITS = OrNode.all(rawFromTo('1', '9'));
    private static TreeNode<Character> DIGITS_WITH_ZERO = new OrNode<>(new ValueNode<>('0'), DIGITS);

    public static TreeNode<Character> numberMatch(long min, long max)
    {
        if (min < 0)
        {
            if (max <= 0)
            {
                return new ConcatNode<>(new ValueNode<>('-'), numberMatch(-max, -min));
            }
            else
            {
                return new OrNode<>(numberMatch(0, max), numberMatch(min, 0));
            }
        }

        if (min > 0)
        {
            throw new RuntimeException("Not yet implemented");
        }

        TreeNode<Character> result = null;
        TreeNode<Character> all = new ValueNode<>('0');
        TreeNode<Character> tenner = null;

        for (; max > 0; )
        {
            int digit = (int) (max % 10);
            max /= 10;

            if (result == null)
            {
                result = OrNode.all(rawFromTo('0', '0' + digit));
            }
            else if (max > 0)
            {
                result = new ConcatNode<>(OrNode.all(rawFromTo('0', '0' + digit)), result);

                if (digit > 0)
                {
                    result = new OrNode<>(result,
                            new ConcatNode<>(OrNode.all(rawFromTo('0', '0' + (digit - 1))), tenner));
                }
            }
            else
            {
                result = new ConcatNode<>(new ValueNode<>((char) ('0' + digit)), result);
            }

            TreeNode<Character> range;
            if (max > 0)
            {
                range = DIGITS;
            }
            else if (digit > 1)
            {
                range = OrNode.all(rawFromTo('1', '0' + (digit - 1)));
            }
            else break;

            if (tenner == null)
            {
                all = new OrNode<>(range, all);
                tenner = DIGITS_WITH_ZERO;
            }
            else
            {
                all = new OrNode<>(new ConcatNode<>(range, tenner), all);
                tenner = new ConcatNode<>(DIGITS_WITH_ZERO, tenner);
            }
        }

        if (result != null)
        {
            all = new OrNode<>(all, result);
        }

        return all;
    }
}
