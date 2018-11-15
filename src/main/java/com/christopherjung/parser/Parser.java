package com.christopherjung.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public abstract class Parser<T>
{
    private ParserInputReader reader;


    public T parse(String str)
    {
        return parse(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
    }

    public T parse(InputStream stream)
    {
        this.reader = new ParserInputReader(stream);
        try
        {
            return parse();
        } catch (ParserInputReader.ParseException e)
        {
            e.printStackTrace();
            return null;
        }
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
