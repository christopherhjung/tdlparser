package com.christopherjung.translator;

import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TDLParser
{
    private ParserTable table;
    private LinkedList<Integer> path;
    private LinkedList<Token> tokens;

    public TDLParser(ParserTable table)
    {
        this.table = table;

        path = new LinkedList<>();
        tokens = new LinkedList<>();
    }

    public void test(ScanResult result)
    {
        int pos = 0;

        path.push(pos);

        Iterator<Token> inputIterator = result.iterator();
        Token current = inputIterator.next();

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
                    current = inputIterator.hasNext() ? inputIterator.next() : null;
                }
                else if (entry.getRestoreActions() >= 0)
                {
                    Rule restoreRule = entry.getRule();
                    List<Token> tokenList = new ArrayList<>(restoreRule.size());

                    for (int i = restoreRule.size() - 1; i >= 0; i--)
                    {
                        String key = restoreRule.getKey(i);
                        Token restoreToken = tokens.pop();

                        if (!restoreToken.getName().equals(key))
                        {
                            throw new RuntimeException("baaaaa");
                        }

                        tokenList.add(0, restoreToken);
                        path.pop();
                    }

                    pos = path.peekFirst();

                    String replace = tokenList.stream().map(Token::getValue).reduce("", (a, b) -> a + b);
                    if (restoreRule.toString().equals("E->E + T") || restoreRule.toString().equals("T->T * F"))
                    {
                        replace = "(" + replace + ")";
                    }

                    tokens.push(new Token(restoreRule.getName(), replace));
                }
                else
                {
                    break;
                }
            }
            else
            {
                Token test = tokens.peekFirst();


                Integer nextPosition = entry.getGoTos().get(test.getName());

                if (nextPosition == null)
                {
                    if (tokens.peekFirst().getName().equals("__start__"))
                    {
                        break;
                    }

                    throw new RuntimeException("dskbdik " + pos);
                }

                path.push(nextPosition);

                pos = nextPosition;
            }
        }

        System.out.println(tokens.pop().getValue());
    }
}
