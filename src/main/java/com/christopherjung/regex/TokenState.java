package com.christopherjung.regex;

import com.christopherjung.scanner.Token;

public class TokenState<T> extends State<T>
{
    private String token;

    public TokenState()
    {
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return token;
    }

    public TokenState<T> propagate(T cha)
    {
        return (TokenState<T>) super.propagate(cha);
    }

    @Override
    public String toString()
    {
        return token + "->" + super.toString();
    }
}
