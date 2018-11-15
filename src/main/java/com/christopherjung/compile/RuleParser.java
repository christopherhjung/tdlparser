package com.christopherjung.compile;

import com.christopherjung.parser.Parser;
import com.christopherjung.regex.ConcatNode;
import com.christopherjung.regex.OrNode;
import com.christopherjung.regex.TreeNode;
import com.christopherjung.regex.ValueNode;

public class RuleParser extends Parser<TreeNode<String>>
{
    @Override
    protected TreeNode<String> parse()
    {
        return new ConcatNode<>(parseOr(),new ValueNode<>());
    }

    protected TreeNode<String> parseOr()
    {
        TreeNode<String> rule = parseConcat();

        if (eat('|'))
        {
            eatWhitespace();
            TreeNode<String> or = parseOr();

            return new OrNode<>(rule, or);
        }

        return rule;
    }

    protected TreeNode<String> parseConcat()
    {
        TreeNode<String> rule = null;

        while (hasNext() && !is('|'))
        {
            TreeNode<String> token = parseToken();

            if (rule == null)
            {
                rule = token;
            }
            else
            {
                rule = new ConcatNode<>(rule, token);
            }
        }


        return rule;
    }

    protected TreeNode<String> parseToken()
    {
        ValueNode<String> valueNode = new ValueNode<>(fetchWhile(cha -> cha != ' ' && cha != '|'));
        eatWhitespace();
        return valueNode;
    }

}
