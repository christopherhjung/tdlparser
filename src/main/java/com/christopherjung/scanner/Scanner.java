package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner
{
    private List<TokenDescriptor> tokenDescriptors = new ArrayList<>();

    public void addAll(InputStream stream)
    {
        ParserInputReader inputReader = new ParserInputReader(stream);
        while (inputReader.hasNext())
        {
            inputReader.eatWhitespace();

            String token = inputReader.fetchUntil(" ");
            inputReader.eatWhitespace();
            String regEx = inputReader.fetchWhile( cha -> !Character.isWhitespace(cha));

            add(token, regEx);
        }
    }

    public void add(String token, String regEx)
    {
        add(TokenDescriptor.create(token, regEx));
    }

    public void add(TokenDescriptor tokenDescriptor)
    {
        tokenDescriptors.add(tokenDescriptor);
    }

    public ScanResult scan(InputStream inputStream)
    {
        List<Token> tokens = new ArrayList<>();

        ParserInputReader reader = new ParserInputReader(inputStream);

        loop:
        while (reader.hasNext())
        {
            for (TokenDescriptor tokenDescriptor : tokenDescriptors)
            {
                Token token = tokenDescriptor.fetchToken(reader);

                if (token != null)
                {
                    if (!tokenDescriptor.getName().equalsIgnoreCase("ignore"))
                    {
                        tokens.add(token);
                    }

                    continue loop;
                }
            }
            break;
        }

        return new ScanResult(tokens);
    }


    @Override
    public String toString()
    {
        return tokenDescriptors.toString();
    }
}
