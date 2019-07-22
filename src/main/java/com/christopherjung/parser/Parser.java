package com.christopherjung.parser;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

public class Parser
{
    private ParserTable table;
    private ModifierSource source;
    private LinkedList<Integer> path;
    private LinkedList<Token> tokens;


    //lookahead????

    public Parser(ParserTable table, ModifierSource source)
    {
        this.table = table;
        this.source = source;

        path = new LinkedList<>();
        tokens = new LinkedList<>();
    }

    public Object parse(ScanJob job)
    {
        Object tag = source.createTag();
        int currentPosition = 0;

        path.clear();
        tokens.clear();

        path.push(currentPosition);

        Token currentToken = job.next();

        for (; ; )
        {
            ParserTable.Entry entry = table.getEntry(currentPosition);

            if (path.size() > tokens.size())
            {
                Integer nextPosition = entry.getAction(currentToken);

                if (nextPosition != null)
                {
                    //aufleiten
                    currentPosition = nextPosition;
                    path.push(currentPosition);
                    tokens.push(currentToken);
                    currentToken = job.next();
                }
                else
                {
                    //ableiten
                    Rule restoreRule = entry.getRestoreRule(currentToken);

                    //multiple restore rules??????????
                    //get right rule from lookahead!!!

                    if (restoreRule != null)
                    {
                        Modifier modifier = source.getModifier(restoreRule);

                        for (int i = restoreRule.size(); --i >= 0;)
                        {
                            Token restoreToken = tokens.pop();
                            modifier.register(i, restoreToken.getValue());

                            path.pop();
                        }

                        currentPosition = path.peekFirst();
                        tokens.push(new Token(restoreRule.getName(), modifier.modify(tag)));
                    }
                    else if (table.isIgnore(currentToken.getName()))
                    {
                        currentToken = job.next();
                    }
                    else break;
                }
            }
            else
            {
                //mit abgeleiteter regel fort
                Integer nextPosition = entry.getGoTo(tokens.peekFirst());

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

            if (!top.getName().equals(grammar.getRoot().getName()))
            {
                throw new RuntimeException("False End Token " + top.getName() + " " + top.getValue());
            }

            return top.getValue();
        }

        System.out.println(tokens);
        System.out.println(table.getEntry(currentPosition));
        Set<String> set = table.getEntry(currentPosition).getActions().keySet();
        throw new ParseException("Exected: " + set);
    }
}
