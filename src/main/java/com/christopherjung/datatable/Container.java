package com.christopherjung.datatable;

public class Container<T>
{
    private T value;

    public Container(T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof Container)
        {
            Container container = (Container) obj;
            return value.equals(container.value);
        }

        return value.equals(obj);
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}
