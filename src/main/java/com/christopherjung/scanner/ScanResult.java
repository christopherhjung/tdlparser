package com.christopherjung.scanner;

import java.util.Iterator;
import java.util.List;

public class ScanResult implements Iterable<Token>
{
    private List<Token> result;

    public ScanResult(List<Token> result)
    {
        this.result = result;
    }

    public int size()
    {
        return result.size();
    }

    public Token get(int pos)
    {
        return result.get(pos);
    }


    @Override
    public Iterator<Token> iterator()
    {
        return result.iterator();
    }

    @Override
    public String toString()
    {
        return result.toString();
    }
}
