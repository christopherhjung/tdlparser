package com.christopherjung.scanner;

import java.util.Objects;

public class Token
{
    public static final Token EOF = new Token("EOF", (String)null);

    private Object value;
    private String name;

    public Token(String name, Object value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.format("[%s:%s]", getName(), Objects.toString(value).replaceAll("\\s+"," "));
    }
}