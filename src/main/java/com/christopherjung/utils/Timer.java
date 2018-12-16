package com.christopherjung.utils;

import java.util.HashMap;

public class Timer
{
    private static HashMap<String, Timer> timers = new HashMap<>();

    public static void resetStart(String name)
    {
        Timer timer = timers.computeIfAbsent(name, Timer::new);
        timer.resetStart();
    }

    public static void reset(String name)
    {
        Timer timer = timers.computeIfAbsent(name, Timer::new);
        timer.reset();
    }

    public static void addTime(String name)
    {
        timers.get(name).add();
    }

    public static void printOverview()
    {
        for (Timer timer : timers.values())
        {
            System.out.println(timer);
        }
    }

    private long start;
    private long sum;
    private long count;
    private String name;

    public Timer(String name)
    {
        this.name = name;
    }

    private void reset()
    {
        sum = 0;
    }

    private long resetStart()
    {
        count++;
        return start = System.currentTimeMillis();
    }

    private void add()
    {
        sum += -start + (start = System.currentTimeMillis());
    }

    @Override
    public String toString()
    {
        return name + " : " + sum + "ms with " + count + " counts";
    }
}
