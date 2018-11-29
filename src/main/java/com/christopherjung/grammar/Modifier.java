package com.christopherjung.grammar;

public interface Modifier
{
    public static Modifier CONCAT = set -> {
        StringBuilder stringBuilder = new StringBuilder();

        for (Object element : set)
        {
            if (element != null)
            {
                stringBuilder.append(element);
            }
        }
        return stringBuilder.toString();
    };

    Object modify(ModifySet set);

}