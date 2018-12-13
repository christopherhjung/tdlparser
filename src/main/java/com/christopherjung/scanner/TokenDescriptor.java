package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;
import com.christopherjung.regex.Pattern;
import com.christopherjung.regex.State;

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
        int beforeLookahead = -1;
        boolean lastLookahead = false;

        for (int currentPosition = 0; inputStream.hasNext(currentPosition); )
        {
            current = current.propagate(inputStream.get(currentPosition++));

            if (current == null)
            {
                break;
            }

            if (current.isLookahead())
            {
                if (!lastLookahead)
                {
                    beforeLookahead = currentPosition;
                }
            }
            else if (lastLookahead)
            {
                currentPosition = beforeLookahead;
            }

            if (current.isAccept())
            {
                length = currentPosition;
            }

            lastLookahead = current.isLookahead();
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
