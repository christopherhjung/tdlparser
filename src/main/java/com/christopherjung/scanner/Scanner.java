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

    public List<ScanResult> scan(InputStream inputStream)
    {
        List<ScanResult> result = new ArrayList<>();

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
                        result.add(new ScanResult(token.getName(), value));
                    }

                    continue loop;
                }
            }

            break;
        }

        return result;
    }


    public static class ScanResult
    {
        String token;
        String value;

        public ScanResult(String token, String value)
        {
            this.token = token;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return String.format("[%s:%s]", token, value);
        }
    }

}
