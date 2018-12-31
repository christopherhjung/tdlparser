package com.christopherjung.parser.simple;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

public class ParserInputReader
{
    private long position;
    private char[] buffer;
    private int head;
    private int tail;
    private InputStream stream;

    public ParserInputReader(InputStream stream)
    {
        this.stream = new BufferedInputStream(stream);

        buffer = new char[10];
        position = 0;
        head = 0;
        tail = 0;
    }

    public void close()
    {
        try
        {
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public long getPosition()
    {
        return position;
    }

    private int rotate(int value, int count)
    {
        return (value + count) % bufferCapacity();
    }

    public int findNext(char cha)
    {
        for (int i = 0; hasNext(); i++)
        {
            if (get(i) == cha)
            {
                return i;
            }
        }

        return -1;
    }

    public String fetch(int chars)
    {
        StringBuilder builder = new StringBuilder();

        while (chars > 0)
        {
            builder.append(eat());
            chars--;
        }

        return builder.toString();
    }

    public String fetchLine()
    {
        String str = fetchWhile(cha -> cha != '\n' && cha != '\r');

        eat('\r');
        eat('\n');

        return str;
    }

    public String fetchOver(String stepOver)
    {
        String result = fetchUntil(stepOver);
        next(stepOver.length());
        return result;
    }

    public String fetchUntil(String limiter)
    {
        StringBuilder builder = new StringBuilder();
        char[] array = limiter.toCharArray();

        int pos = 0;
        for (int i = 0; pos < array.length && hasNext(); i++)
        {
            if (get(i) == array[pos])
            {
                pos++;
            }
            else
            {
                builder.append(array, 0, pos);
                next(pos);
                builder.append(eat());
                i = -1;
                pos = 0;
            }
        }

        return builder.toString();
    }

    public String fetchWhile(Predicate<Character> test)
    {
        StringBuilder builder = new StringBuilder();

        while (hasNext() && test.test(get()))
        {
            builder.append(eat());
        }

        return builder.toString();
    }

    public int bufferSize()
    {
        return head - tail + (head < tail ? buffer.length : 0);
    }

    public int bufferCapacity()
    {
        return buffer.length;
    }

    public char eat()
    {
        char cha = get();
        next();
        return cha;
    }

    public boolean eat(char cha)
    {
        if (get() == cha)
        {
            next();
            return true;
        }
        return false;
    }

    public boolean eat(String str)
    {
        if (followedBy(str))
        {
            next(str.length());
            return true;
        }
        return false;
    }

    public int eatWhitespace()
    {
        int count = 0;
        while (Character.isWhitespace(get()))
        {
            next();
            count++;
        }

        return count;
    }

    public boolean followedBy(String str)
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

    public boolean isValid()
    {
        return get() < 255;
    }

    public void next()
    {
        if (tail == head)
        {
            preLoad(1);
        }

        tail = (tail + 1) % bufferCapacity();
        position++;
    }

    public void unread()
    {
        tail = head;
    }

    public void next(int count)
    {
        if (bufferCapacity() <= count)
        {
            head = 0;
            tail = 0;

            try
            {
                stream.skip(count - bufferCapacity());
            }
            catch (IOException e)
            {
                throw new ParseException("Not possible to skip", e);
            }
        }
        else
        {
            tail = (tail + count) % bufferCapacity();
        }
    }

    public void checkBufferSize(int nextCount)
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

    public void preLoad(int nextCount)
    {
        checkBufferSize(nextCount);

        while (bufferSize() < nextCount)
        {
            try
            {
                buffer[head] = (char) stream.read();
            }
            catch (IOException e)
            {
                throw new ParseException("Not possible to read", e);
            }
            head = (head + 1) % bufferCapacity();
        }
    }

    public boolean hasNext()
    {
        return hasNext(0);
    }

    public boolean hasNext(int pos)
    {
        try
        {
            return get(pos) < 255 ;
        }
        catch (Exception e)
        {
            throw new ParseException("avail", e);
        }
    }

    public boolean is(char cha)
    {
        return get() == cha;
    }

    public char get()
    {
        return get(0);
    }

    public char get(int index)
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