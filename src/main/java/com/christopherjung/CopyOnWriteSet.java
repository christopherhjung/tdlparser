package com.christopherjung;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CopyOnWriteSet<E> implements Set<E>
{
    private Set<E> ref;
    private boolean copy = false;

    public CopyOnWriteSet(Set<E> ref)
    {
        this.ref = ref;
    }

    private void copyIfNotAlready()
    {
        if (!copy)
        {
            ref = new HashSet<>(ref);
            copy = true;
        }else{
            System.out.println("#######################");
        }
    }

    @Override
    public int size()
    {
        return ref.size();
    }

    @Override
    public boolean isEmpty()
    {
        return ref.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return ref.contains(o);
    }

    @Override
    public Iterator<E> iterator()
    {
        return ref.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return ref.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return ref.toArray(a);
    }

    @Override
    public boolean add(E e)
    {
        copyIfNotAlready();
        return ref.add(e);
    }

    @Override
    public boolean remove(Object o)
    {
        copyIfNotAlready();
        return ref.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return ref.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        copyIfNotAlready();
        return ref.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        copyIfNotAlready();
        return ref.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        copyIfNotAlready();
        return ref.removeAll(c);
    }

    @Override
    public void clear()
    {
        copyIfNotAlready();
        ref.clear();
    }

    @Override
    public Spliterator<E> spliterator()
    {
        return ref.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter)
    {
        return ref.removeIf(filter);
    }

    @Override
    public Stream<E> stream()
    {
        return ref.stream();
    }

    @Override
    public Stream<E> parallelStream()
    {
        return ref.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action)
    {
        ref.forEach(action);
    }

    @Override
    public String toString()
    {
        return ref.toString();
    }

    @Override
    public int hashCode()
    {
        return ref.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return ref.equals(obj);
    }
}
