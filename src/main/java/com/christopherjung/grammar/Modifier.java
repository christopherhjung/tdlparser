package com.christopherjung.grammar;

public interface Modifier
{
    Object modify();

    void register(int index, Object obj);
}