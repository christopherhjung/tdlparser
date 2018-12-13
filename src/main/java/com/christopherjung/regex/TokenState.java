package com.christopherjung.regex;

import com.christopherjung.scanner.Token;

public class TokenState<T> extends State<T>
{
    private String token;

    public TokenState(String token)
    {
        this.token = token;
    }
}
