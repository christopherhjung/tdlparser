package com.christopherjung.datatable;

public class NumberUtils
{

    public static Number cast(Number src, Class<?> target){

        if (target == Double.class)
        {
            src = src.doubleValue();
        }
        else if (target == Float.class)
        {
            src = src.floatValue();
        }
        else if (target == Integer.class)
        {
            src = src.intValue();
        }
        else if (target == Byte.class)
        {
            src = src.byteValue();
        }
        else if (target == Short.class)
        {
            src = src.shortValue();
        }
        else if (target == Long.class)
        {
            src = src.longValue();
        }

        return src;
    }
}
