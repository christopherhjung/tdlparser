package com.christopherjung.scanner;

import com.christopherjung.regex.ConcatStates;
import com.christopherjung.regex.Pattern;
import com.christopherjung.regex.State;
import com.christopherjung.regex.TokenState;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
        private Map<String, State<Character>> tokenRegex = new LinkedHashMap<>();

        public void addStructureChars(String structureChars)
        {
            for (char cha : structureChars.toCharArray())
            {
                add(String.valueOf(cha), "\\" + cha);
            }
        }

        public void add(String token, String regEx)
        {
            tokenRegex.put(token, Pattern.compile(regEx));
        }

        public Scanner build()
        {
            return new Scanner(ConcatStates.create(tokenRegex));
        }
    }
}
