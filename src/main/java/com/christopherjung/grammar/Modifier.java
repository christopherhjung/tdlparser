package com.christopherjung.grammar;

public interface Modifier
{
    Object modify( Object tag );

    void register(int index, Object obj);
}