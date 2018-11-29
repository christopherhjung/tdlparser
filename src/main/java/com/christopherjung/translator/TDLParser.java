package com.christopherjung.translator;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.grammar.ModifySet;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
        int pos = 0;

        path.push(pos);


        if (!job.hasNext())
        {
            throw new TLDParseException("No Input tokens provided");
        }

        Token current = job.next();

        for (; ; )
        {
            ParserTable.Entry entry = table.getEntries().get(pos);

            if (path.size() > tokens.size())
            {
                Integer nextPosition = current == null ? null : entry.getActions().get(current.getName());

                if (nextPosition != null)
                {
                    pos = nextPosition;

                    path.push(pos);
                    tokens.push(current);
                    current = job.hasNext() ? job.next() : null;
                }
                else
                {
                    if (entry.getRestoreAction() >= 0)
                    {
                        Rule restoreRule = entry.getRule();
                        List<Object> objects = new ArrayList<>(restoreRule.size());

                        for (int i = restoreRule.size() - 1; i >= 0; i--)
                        {
                            String key = restoreRule.getKey(i);
                            Token restoreToken = tokens.pop();

                            if (!restoreToken.getName().equals(key))
                            {
                                throw new TLDParseException("Parser Table wrong");
                            }

                            objects.add(0, restoreToken.getValue());
                            path.pop();
                        }

                        pos = path.peekFirst();

                        Modifier modifier = source.getModifier(restoreRule);
                        Object modifiedToken;
                        if (modifier != null)
                        {
                            ModifySet modifySet = new ModifySet(objects);
                            modifiedToken = modifier.modify(modifySet);
                        }
                        else
                        {
                            modifiedToken = objects.stream().reduce("", (a, b) -> a + "" + b);
                        }

                        tokens.push(new Token(restoreRule.getName(), modifiedToken));
                    }
                    else if (current != null && entry.isIgnore(current.getName()))
                    {
                        System.out.println(current);
                        current = job.hasNext() ? job.next() : null;
                        System.out.println(current);
                    }
                    else
                    {
                        break;
                    }
                }
            }
            else
            {
                Token test = tokens.peekFirst();

                Integer nextPosition = entry.getGoTos().get(test.getName());

                if (nextPosition == null)
                {
                    break;
                }

                path.push(nextPosition);

                pos = nextPosition;
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
