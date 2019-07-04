package com.christopherjung.reflectparser;

import com.christopherjung.container.*;
import com.christopherjung.parser.simple.Parser;

public class RuleParser extends Parser<TreeNode<String>>
{
    @Override
    protected TreeNode<String> parse()
    {
        return parseOr();
    }

    protected TreeNode<String> parseOr()
    {
        TreeNode<String> state = parseConcat();

        eatWhitespace();
        if (eat('|'))
        {
            TreeNode<String> right = parseOr();
            return new OrNode<>(state, right);
        }

        return state;
    }

    protected TreeNode<String> parseConcat()
    {
        TreeNode<String> state = parseMultiplier();
        eatWhitespace();
        if (hasNext() && !is('|') && !is(')'))
        {
            return new ConcatNode<>(state, parseConcat());
        }

        return state;
    }

    protected TreeNode<String> parseMultiplier()
    {
        TreeNode<String> state = parseParenthesis();

        if (hasNext())
        {
            eatWhitespace();
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
            else if (eat('['))
            {

                String value = parseNameOrString();

                if (!eat('}'))
                {
                    throw new RuntimeException("kfdhbsn");
                }

                return new SeperatorNode<>(state, value);
            }
        }

        return state;
    }

    protected TreeNode<String> parseParenthesis()
    {
        if (eat('('))
        {
            TreeNode<String> temp = parseOr();
            if (!eat(')'))
            {
                throw new RuntimeException("No Parenthesis closing! " + fetch(10) + temp.toRegEx());
            }
            return temp;
        }
        else
        {
            return parseValue();
        }
    }


    public TreeNode<String> parseValue()
    {
        eatWhitespace();
        String value = parseNameOrString();

        if (eat(':'))
        {
            return new NameNode<>(value, parseMultiplier());
        }

        return new ValueNode<>(value);
    }

    public String parseNameOrString()
    {
        eatWhitespace();
        if (eat('\''))
        {
            return fetchOver("\'");
        }
        else
        {
            return parseName();
        }
    }

    public String parseName()
    {
        return fetchWhile(cha -> Character.isDigit(cha) || Character.isLetter(cha));
    }
}
