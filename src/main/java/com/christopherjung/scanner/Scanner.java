package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Scanner
{
    private ArrayList<Token> tokens = new ArrayList<>();

    public void add(String token, String regEx)
    {
        add(Token.create(token, regEx));
    }

    public void add(Token token)
    {
        tokens.add(token);
    }

    public ScanResult scan(InputStream inputStream)
    {
        ScanResult result = new ScanResult();

        ParserInputReader reader = new ParserInputReader(inputStream);

        loop:while (reader.hasNext())
        {
            for (Token token : tokens)
            {
                String value = token.test(reader);

                if (value != null)
                {
                    if (!token.getName().equalsIgnoreCase("ignore"))
                    {
                        result.addResult(token.getName(),value);
                    }

                    continue loop;
                }
            }

            break;
        }

        return result;
    }




}
