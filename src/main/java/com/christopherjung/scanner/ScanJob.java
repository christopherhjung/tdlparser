package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;

import java.io.InputStream;
import java.util.Arrays;

public class ScanJob
{
    private Scanner scanner;
    private ParserInputReader reader;
    private boolean finished = false;

    public ScanJob(Scanner scanner, InputStream inputStream)
    {
        this.scanner = scanner;
        this.reader = new ParserInputReader(inputStream);
    }

    public Token next()
    {
        loop:
        while (reader.hasNext())
        {
            if (scanner.getStructureChars() != null)
            {
                int index = Arrays.binarySearch(scanner.getStructureChars(), reader.get());

                if (index >= 0)
                {
                    String name = String.valueOf(reader.eat());
                    return new Token(name, name);
                }
            }

            for (TokenDescriptor tokenDescriptor : scanner.getTokenDescriptors())
            {
                Token token = tokenDescriptor.fetchToken(reader);

                if (token != null)
                {
                    if (!tokenDescriptor.getName().equalsIgnoreCase("ignore"))
                    {
                        return token;
                    }

                    continue loop;
                }
            }

            throw new RuntimeException("Scanner error :" + reader.fetch(20) + "...");
        }

        reader.close();
        finished = true;
        return Token.EOF;
    }

    public boolean hasNext()
    {
        return !finished;
    }
}

