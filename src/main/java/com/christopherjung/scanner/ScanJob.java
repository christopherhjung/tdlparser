package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;

import java.io.InputStream;
import java.util.Arrays;
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

        long start = System.currentTimeMillis();
        long end;

        while (reader.hasNext())
        {
            if (scanner.getStructureChars() != null)
            {
                int index = Arrays.binarySearch(scanner.getStructureChars(), reader.get());

                if (index >= 0)
                {
                    String name = String.valueOf(reader.eat());

                    end = System.currentTimeMillis();
                    timeNeed += end - start;

                    return new Token(name, name);
                }
            }

            if (fetchToken(scanner.getIgnoreDescriptors()) == null)
            {
                Token token = fetchToken(scanner.getTokenDescriptors());

                if (token == null)
                {
                    throw new RuntimeException("Scanner error :" + reader.fetch(20) + "...");
                }

                end = System.currentTimeMillis();
                timeNeed += end - start;
                return token;
            }
        }


        System.out.println("Scanner: " + timeNeed);

        finished = true;
        reader.close();
        return Token.EOF;
    }

    private Token fetchToken(List<TokenDescriptor> tokenDescriptors)
    {
        for (TokenDescriptor tokenDescriptor : tokenDescriptors)
        {
            Token token = tokenDescriptor.fetchToken(reader);

            if (token != null)
            {
                return token;
            }
        }

        return null;
    }

    public boolean hasNext()
    {
        return !finished;
    }
}

