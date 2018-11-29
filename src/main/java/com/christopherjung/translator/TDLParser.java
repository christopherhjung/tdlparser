package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.grammar.ModifySet;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Token;

import java.util.Arrays;
import java.util.LinkedList;

public class TDLParser
{
    private ParserTable table;
    private ModifierSource source;
    private LinkedList<Integer> path;
    private LinkedList<Token> tokens;

    public TDLParser(ParserTable table, ModifierSource source)
    {
        this.table = table;
        this.source = source;

        path = new LinkedList<>();
        tokens = new LinkedList<>();
    }

    public Object parse(ScanJob job)
    {
        int currentPosition = 0;

        path.push(currentPosition);


        if (!job.hasNext())
        {
            throw new TLDParseException("No Input tokens provided");
        }

        Token currentToken = job.next();

        for (; ; )
        {
            ParserTable.Entry entry = table.getEntries().get(currentPosition);

            if (path.size() > tokens.size())
            {
                Integer nextPosition = currentToken == null ? null : entry.getActions().get(currentToken.getName());

                if (nextPosition != null)
                {
                    currentPosition = nextPosition;

                    path.push(currentPosition);
                    tokens.push(currentToken);
                    currentToken = job.hasNext() ? job.next() : null;
                }
                else
                {
                    if (entry.hasRestoreRule())
                    {
                        Rule restoreRule = entry.getRule();
                        Object[] objects = new Object[restoreRule.size()];

                        for (int i = restoreRule.size() - 1; i >= 0; i--)
                        {
                            String key = restoreRule.getKey(i);
                            Token restoreToken = tokens.pop();

                            if (!restoreToken.getName().equals(key))
                            {
                                throw new TLDParseException("Parser Table wrong");
                            }

                            objects[i] = restoreToken.getValue();
                            path.pop();
                        }

                        currentPosition = path.peekFirst();

                        Modifier modifier = source.getModifier(restoreRule);
                        Object modifiedToken;
                        if (modifier != null)
                        {
                            ModifySet modifySet = new ModifySet(objects);
                            modifiedToken = modifier.modify(modifySet);
                        }
                        else
                        {
                            modifiedToken = Arrays.stream(objects).reduce("", (a, b) -> a + "" + b);
                        }

                        tokens.push(new Token(restoreRule.getName(), modifiedToken));
                    }
                    else if (currentToken != null && entry.isIgnore(currentToken.getName()))
                    {
                        currentToken = job.hasNext() ? job.next() : null;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            else
            {
                Token ruleName = tokens.peekFirst();

                Integer nextPosition = entry.getGoTos().get(ruleName.getName());

                if (nextPosition == null)
                {
                    break;
                }

                path.push(nextPosition);

                currentPosition = nextPosition;
            }
        }

        if (tokens.size() == 1)
        {
            Grammar grammar = table.getGrammar();
            Token top = tokens.peekFirst();

            if (!top.getName().equals(grammar.getRootRule().getName()))
            {
                throw new RuntimeException("False End Token " + top.getName() + " " + top.getValue());
            }

            return top.getValue();
        }

        throw new TLDParseException("Token result size not equals 1 " + tokens);
    }
}
