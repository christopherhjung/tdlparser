package com.christopherjung.reflectparser;

public class MathParser
{
    @ScannerStructure
    public static String structureChars = "+-/*()";

    @ScannerToken
    public static String number = "\\d+";

    @ScannerToken
    public static String variable = "[a-zA-Z]\\w+";

    @ScannerIgnore
    public static String space = "\\s+";


    @RootNode("add EOF")
    public static String start(String add)
    {
        return add;
    }

    @Node("add -> add sign:- mul")
    @Node("add -> add sign:+ mul")
    public static String add(String add, String sign, String mul)
    {
        return "(" + add + " " + sign + " " + mul + ")";
    }

    @Node("add -> mul")
    public static String add(String mul)
    {
        return mul;
    }

    @Node("mul -> mul sign:* statement")
    @Node("mul -> mul sign:/ statement")
    public static String mul(String mul, String sign, String statement)
    {
        return "(" + mul + " " + sign + " " + statement + ")";
    }

    @Node("mul -> statement")
    public static String mul(String statement)
    {
        return statement;
    }

    @Node("statement -> ( add )")
    public static String parenth(String add)
    {
        return "(" + add + ")";
    }

    @Node("statement -> key:variable")
    @Node("statement -> key:number")
    public static String key(String key)
    {
        return key;
    }
}
