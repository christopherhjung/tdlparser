package com.christopherjung.scanner;

public class Token
{
    private TokenDescriptor descriptor;
    private String value;
    private String name;

    public Token(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public Token(String value, TokenDescriptor descriptor)
    {
        this.descriptor = descriptor;
        this.value = value;
    }

    public String getName()
    {
        return descriptor == null ? name : descriptor.getName();
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.format("[%s:%s]", getName(), value.replaceAll("\\s+"," "));
    }
}