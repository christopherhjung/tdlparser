package com.christopherjung.utils;

import java.io.*;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

public class StreamUtils
{
    public static InputStream getFileStream(String filename)
    {
        try
        {
            File file = new File(StreamUtils.class.getClassLoader().getResource(filename).getFile());
            return new BufferedInputStream(new FileInputStream(file));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(String filename)  {

        try (Scanner scanner = new Scanner(getFileStream(filename))) {
            return scanner.useDelimiter("\\A").next();
        }

    }

    public static <T> T loopStringWithResult(String src, Function<InputStream, T> supplier)
    {
        try
        {
            InputStream stream = new ByteArrayInputStream(src.getBytes("UTF-8"));
            T scanResult = supplier.apply(stream);
            stream.close();
            return scanResult;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static <T> T loopFileWithResult(String filename, Function<InputStream, T> supplier)
    {
        try
        {
            File file = new File(StreamUtils.class.getClassLoader().getResource(filename).getFile());
            InputStream stream = new FileInputStream(file);
            T scanResult = supplier.apply(stream);
            stream.close();
            return scanResult;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void loopFile(String filename, Consumer<InputStream> supplier)
    {
        try
        {
            File file = new File(StreamUtils.class.getClassLoader().getResource(filename).getFile());
            InputStream stream = new FileInputStream(file);
            supplier.accept(stream);
            stream.close();
        }
        catch (Exception e)
        {
        }
    }
}