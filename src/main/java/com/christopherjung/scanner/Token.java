package com.christopherjung.scanner;

import com.christopherjung.machine.Pattern;
import com.christopherjung.machine.State;
import com.christopherjung.parser.ParserInputReader;

public class Token
{
    private String name;
    private State begin;

    private Token(String name, State begin)
    {
        this.name = name;
        this.begin = begin;
    }

    public String getName()
    {
        return name;
    }

    public String test(ParserInputReader inputStream)
    {
        State current = begin;

        int length = -1;

        for (int i = 0; ; )
        {
            char cha = inputStream.get(i++);
            current = current.propagate(cha);

            if (current == null)
            {
                break;
            }
            else if (current.isAccept())
            {
                length = i;
            }
        }

        if (length >= 0)
        {
            return inputStream.fetch(length);
        }

        return null;
    }


    public static Token create(String name, String regEx)
    {
        return new Token(name, Pattern.compile(regEx));
    }
}
