package com.christopherjung.compile;


import com.christopherjung.parser.Parser;

public class RuleParser extends Parser<TreeNode<String>>
{
    @Override
    protected TreeNode<String> parse()
    {
        return TreeNode.close(parseOr());
    }

    public TreeNode<String> parseRaw(String str)
    {
        reset(str);
        return parseOr();
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
