package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner
{
    private List<TokenDescriptor> tokenDescriptors;
    private char[] structureChars;

    public Scanner(List<TokenDescriptor> tokenDescriptors)
    {
        this.tokenDescriptors = new ArrayList<>(tokenDescriptors);
    }

    public Scanner(List<TokenDescriptor> tokenDescriptors, String structureChars)
    {
        this.tokenDescriptors = new ArrayList<>(tokenDescriptors);
        this.structureChars = structureChars.toCharArray();

        Arrays.sort(this.structureChars);
    }

    public ScanResult scan(InputStream inputStream)
    {
        List<Token> tokens = new ArrayList<>();

        ScanJob job = new ScanJob(this,inputStream);

        while (true)
        {
            Token token = job.next();

            tokens.add(token);

            if (token == Token.EOF)
            {
                break;
            }
        }

        return new ScanResult(tokens);
    }

    public char[] getStructureChars()
    {
        return structureChars;
    }

    public List<TokenDescriptor> getTokenDescriptors()
    {
        return tokenDescriptors;
    }

    @Override
    public String toString()
    {
        return tokenDescriptors.toString();
    }

    public static class Builder
    {
        private List<TokenDescriptor> tokenDescriptors = new ArrayList<>();
        private StringBuilder structureCharsBuilder = new StringBuilder();

        public void addAll(InputStream stream)
        {
            ParserInputReader inputReader = new ParserInputReader(stream);
            while (inputReader.hasNext())
            {
                inputReader.eatWhitespace();

                String token = inputReader.fetchUntil(" ");
                inputReader.eatWhitespace();
                String regEx = inputReader.fetchWhile(cha -> !Character.isWhitespace(cha));

                add(token, regEx);
            }
        }

        public void addStructureChars(String structureChars)
        {
            structureCharsBuilder.append(structureChars);
        }

        public void add(String token, String regEx)
        {
            add(TokenDescriptor.create(token, regEx));
        }

        public void add(TokenDescriptor tokenDescriptor)
        {
            tokenDescriptors.add(tokenDescriptor);
        }

        public Scanner build()
        {
            return new Scanner(tokenDescriptors, structureCharsBuilder.toString());
        }
    }
}
