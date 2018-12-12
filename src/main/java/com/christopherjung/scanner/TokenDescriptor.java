package com.christopherjung.scanner;

import com.christopherjung.regex.Pattern;
import com.christopherjung.regex.State;
import com.christopherjung.parser.ParserInputReader;

public class TokenDescriptor
{
    private String name;
    private String regEx;
    private State<Character> begin;

    private TokenDescriptor(String name, String regEx)
    {
        this.name = name;
        this.regEx = regEx;
        this.begin = Pattern.compile(regEx);
    }

    public String getName()
    {
        return name;
    }

    public Token fetchToken(ParserInputReader inputStream)
    {
        String value = fetch(inputStream);

        if (value == null)
        {
            return null;
        }

        return new Token(value, this);
    }

    public String fetch(ParserInputReader inputStream)
    {
        State<Character> current = begin;

        int length = -1;

        for (int i = 0; inputStream.hasNext(i); )
        {
            current = current.propagate(inputStream.get(i++));

            if (current == null)
            {
                break;
            }
            else if (current.isAccept())
            {
                length = i;
            }
        }

        if (length > 0)
        {
            return inputStream.fetch(length);
        }

        return null;
    }

    @Override
    public String toString()
    {
        return "TokenDescriptor(" + name + ", " + regEx + ")";
    }

    public static TokenDescriptor create(String name, String regEx)
    {
        return new TokenDescriptor(name, regEx);
    }
}
