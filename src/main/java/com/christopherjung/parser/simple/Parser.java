package com.christopherjung.parser.simple;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public abstract class Parser<T>
{
    private ParserInputReader reader;

    protected void reset(String str)
    {
        reset(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
    }

    protected void reset(InputStream inputStream)
    {
        this.reader = new ParserInputReader(inputStream);
    }

    public T parse(String str)
    {
        reset(str);
        return parse();
    }

    public T parse(InputStream stream)
    {
        reset(stream);
        return parse();
    }

    protected abstract T parse();

    public long getPosition()
    {
        return reader.getPosition();
    }

    protected String fetch(int chars)
    {
        return reader.fetch(chars);
    }

    protected String fetchUntil(String limiter)
    {
        return reader.fetchUntil(limiter);
    }

    protected String fetchOver(String limiter)
    {
        return reader.fetchOver(limiter);
    }

    protected String fetchWhile(Predicate<Character> test)
    {
        return reader.fetchWhile(test);
    }

    protected int findNext(char cha)
    {
        return reader.findNext(cha);
    }

    protected char eat()
    {
        return reader.eat();
    }

    protected boolean eat(char cha)
    {
        return reader.eat(cha);
    }

    protected boolean eat(String str)
    {
        return reader.eat(str);
    }

    protected int eatWhitespace()
    {
        return reader.eatWhitespace();
    }

    protected boolean followedBy(String str)
    {
        return followedBy(str);
    }

    protected boolean isValid()
    {
        return reader.isValid();
    }

    protected void next()
    {
        reader.next();
    }

    protected void next(int count)
    {
        reader.next(count);
    }

    protected boolean hasNext()
    {
        return reader.hasNext();
    }

    protected boolean is(char cha)
    {
        return reader.is(cha);
    }

    protected char get()
    {
        return reader.get();
    }

    protected char get(int index)
    {
        return reader.get(index);
    }
}
