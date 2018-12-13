package com.christopherjung.scanner;

import com.christopherjung.parser.ParserInputReader;
import com.christopherjung.regex.ConcatStates;
import com.christopherjung.regex.Pattern;
import com.christopherjung.regex.State;
import com.christopherjung.regex.TokenState;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner
{
    private TokenState<Character> tokenState;

    public Scanner(TokenState<Character> tokenState)
    {
        this.tokenState = tokenState;
    }

    public TokenState<Character> getTokenState()
    {
        return tokenState;
    }

    public static class Builder
    {
        private Map<String, State<Character>> tokenRegex = new HashMap<>();

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
            for (char cha : structureChars.toCharArray())
            {
                add(cha + "", "\\" + cha);
            }
        }

        public void add(String token, String regEx)
        {
            tokenRegex.put(token, Pattern.compile(regEx));
        }

        public Scanner build()
        {
            TokenState<Character> full = ConcatStates.create(tokenRegex);

            return new Scanner(full);
        }
    }
}
