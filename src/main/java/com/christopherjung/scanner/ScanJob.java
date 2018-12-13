package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;
import com.christopherjung.regex.TokenState;

import java.io.InputStream;
import java.util.List;

public class ScanJob
{
    private Scanner scanner;
    private ParserInputReader reader;
    private boolean finished = false;

    private long timeNeed = 0;

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
                TokenState<Character> next = current.propagate(reader.get(i++));

                if (next == null)
                {
                    if (current.getToken().equals("ignore"))
                    {
                        reader.next(target);
                        break;
                    }

                    String str = reader.fetch(target);

                    return new Token(current.getToken(), str);
                }

                if (!next.isLookahead())
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

