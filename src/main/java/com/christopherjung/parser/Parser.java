package com.christopherjung.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class Parser<T>
{
    private long position;
    private char[] buffer;
    private int head;
    private int tail;
    private InputStream stream;

    public long getPosition()
    {
        return position;
    }

    private int rotate(int value, int count)
    {
        return (value + count) % bufferCapacity();
    }

    protected String fetch(int chars)
    {
        StringBuilder builder = new StringBuilder();

        while (chars > 0)
        {
            builder.append(get());
            next();
            chars--;
        }

        return builder.toString();
    }

    public T parse(String str)
    {
        return parse(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
    }

    public T parse(InputStream stream)
    {
        this.stream = stream;
        buffer = new char[10];
        position = 0;
        head = 0;
        tail = 0;
        try
        {
            return parse();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    protected abstract T parse();

    protected int bufferSize()
    {
        return head - tail + (head < tail ? buffer.length : 0);
    }

    protected int bufferCapacity()
    {
        return buffer.length;
    }

    protected char eat()
    {
        char cha = get();
        next();
        return cha;
    }

    protected boolean eat(char cha)
    {
        if (get() == cha)
        {
            next();
            return true;
        }
        return false;
    }

    protected boolean eat(String str)
    {
        if (followedBy(str))
        {
            next(str.length());
            return true;
        }
        return false;
    }

    protected int eatWhitespace()
    {
        int count = 0;
        while (Character.isWhitespace(get()))
        {
            next();
            count++;
        }

        return count;
    }

    protected boolean followedBy(String str)
    {
        int index = 0;
        preLoad(str.length());
        for (char cha : str.toCharArray())
        {
            if (cha != get(index))
            {
                return false;
            }

            index++;
        }

        return true;
    }

    protected boolean isValid()
    {
        return get() < 255;
    }

    protected void next()
    {
        if (tail == head)
        {
            preLoad(1);
        }

        tail = (tail + 1) % bufferCapacity();
        position++;
    }

    protected void next(int count)
    {
        if (bufferCapacity() <= count)
        {
            head = 0;
            tail = 0;

            try
            {
                stream.skip(count - bufferCapacity());
            } catch (IOException e)
            {
                throw new ParseException("Not possible to skip", e);
            }
        }
        else
        {
            tail = (tail + count) % bufferCapacity();
        }
    }

    protected void checkBufferSize(int nextCount)
    {
        if (nextCount >= buffer.length)
        {
            int newSize = Math.max(nextCount, bufferCapacity()) << 1;
            char[] newArray = new char[newSize];
            if (head < tail)
            {
                int first = bufferCapacity() - tail;
                System.arraycopy(buffer, tail, newArray, 0, first);
                System.arraycopy(buffer, 0, newArray, first, bufferSize() - first);
            }
            else
            {
                System.arraycopy(buffer, tail, newArray, 0, head - tail);
            }

            head = bufferSize();
            tail = 0;
            buffer = newArray;
        }
    }

    protected void preLoad(int nextCount)
    {
        checkBufferSize(nextCount);

        while (bufferSize() < nextCount)
        {
            try
            {
                buffer[head] = (char) stream.read();
            } catch (IOException e)
            {
                throw new ParseException("Not possible to read", e);
            }
            head = (head + 1) % bufferCapacity();
        }
    }

    protected boolean hasNext()
    {
        try
        {
            return get() < 255 && (stream.available() > 0 || bufferSize() > 0);
        } catch (Exception e)
        {
            throw new ParseException("avail", e);
        }
    }

    protected boolean is(char cha)
    {
        return get() == cha;
    }

    protected char get()
    {
        return get(0);
    }

    protected char get(int index)
    {
        /*if (index >= bufferCapacity())
        {
            throw new RuntimeException("sss");
        }*/

        if (index >= bufferSize())
        {
            preLoad(index + 1);
        }

        return buffer[(index + tail) % bufferCapacity()];
    }


    public static class ParseException extends RuntimeException
    {
        public ParseException(String message)
        {
            super(message);
        }

        public ParseException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}
