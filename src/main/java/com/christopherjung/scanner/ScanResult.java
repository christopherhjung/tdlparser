package com.christopherjung.scanner;

import java.util.ArrayList;
import java.util.List;

public class ScanResult
{
    private List<Entry> result = new ArrayList<>();

    public void addResult(String token, String value)
    {
        result.add(new Entry(token, value));
    }

    public int size()
    {
        return result.size();
    }

    public Entry get(int pos)
    {
        return result.get(pos);
    }

    public static class Entry
    {
        String token;
        String value;

        public Entry(String token, String value)
        {
            this.token = token;
            this.value = value;
        }

        public String getToken()
        {
            return token;
        }

        public void setToken(String token)
        {
            this.token = token;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return String.format("[%s:%s]", token, value);
        }
    }
}
