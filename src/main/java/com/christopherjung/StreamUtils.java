package com.christopherjung;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.function.Function;

public class StreamUtils
{

    public static <T> T loopFile(String filename, Function<InputStream, T> supplier)
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
            return null;
        }
    }
}
