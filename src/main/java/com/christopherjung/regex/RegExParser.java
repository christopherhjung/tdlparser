package com.christopherjung.regex;

import com.christopherjung.parser.Parser;
import com.christopherjung.parser.ParserInputReader;

import java.util.HashSet;

public class RegExParser extends Parser<TreeNode<Character>>
{
    @Override
    protected TreeNode<Character> parse()
    {
        return new ConcatNode<>(parseOr(), new ValueNode<>());
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
                    TreeNode<Character> cloned = state.clone();

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
                return OrNode.all(ValueNode.rawFromTo(0, 127));
            }
            else if (eat('['))
            {
                boolean negate = eat('^');

                HashSet<Character> nodes = new HashSet<>();

                char save = 255;

                while (true)
                {
                    if (save != 255 && eat('-'))
                    {
                        ValueNode.fromToChars(nodes, save, eat());
                        save = 255;
                    }
                    else
                    {
                        if (save != 255)
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
                    ValueNode.fromToChars(complement, 0, 127);
                    complement.removeAll(nodes);
                    nodes = complement;
                }

                return OrNode.all(nodes);
            }
            else if (eat('\\'))
            {
                if (eat('d'))
                {
                    return OrNode.all(ValueNode.rawFromTo('0', '9'));
                }
                else if (eat('s'))
                {
                    return OrNode.all(ValueNode.rawOf('\n', ' ', '\t', '\r'));
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


}
