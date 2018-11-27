package com.christopherjung;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

public class StreamUtils
{

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
            File file = new File(Main.class.getClassLoader().getResource(filename).getFile());
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
            File file = new File(Main.class.getClassLoader().getResource(filename).getFile());
            InputStream stream = new FileInputStream(file);
            supplier.accept(stream);
            stream.close();
        }
        catch (Exception e)
        {
        }
    }
}
