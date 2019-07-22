package com.christopherjung.scanner;

import com.christopherjung.parser.simple.ParserInputReader;
import com.christopherjung.regex.TokenState;

import java.io.InputStream;

public class ScanJob
{
    private Scanner scanner;
    private ParserInputReader reader;
    private boolean finished = false;
    private long position = 0;


    public ScanJob(Scanner scanner, InputStream inputStream)
    {
        this.scanner = scanner;
        this.reader = new ParserInputReader(inputStream);
    }

    public Token next()
    {
        if (finished)
        {
            return Token.EOF;
        }

        while (reader.hasNext())
        {
            TokenState<Character> current = scanner.getTokenState();
            int target = -1;
            for (int i = 0; ; )
            {
                char currentChar = reader.get(i++);
                TokenState<Character> next = current.propagate(currentChar);

                if (next == null)
                {
                    if ("ignore".equals(current.getToken()))
                    {
                        reader.next(target);
                        break;
                    }

                    String str = reader.fetch(target);

                    if (current.getToken() == null)
                    {
                        throw new RuntimeException("Unknown token " + str + " - " + current );
                    }

                    return new Token(current.getToken(), str);
                }

                if (!current.isLookahead(currentChar))
                {
                    target = i;
                }

                current = next;
            }
        }

        finished = true;
        reader.close();
        return Token.EOF;
    }

    public boolean hasNext()
    {
        return !finished;
    }
}

