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


    @ParserRoot("add EOF")
    public static String start(String add)
    {
        return add;
    }

    @ParserRule("add -> add sign:- mul")
    @ParserRule("add -> add sign:+ mul")
    public static String add(String add, String sign, String mul)
    {
        return "(" + add + " " + sign + " " + mul + ")";
    }

    @ParserRule("add -> mul")
    public static String add(String mul)
    {
        return mul;
    }

    @ParserRule("mul -> mul sign:* statement")
    @ParserRule("mul -> mul sign:/ statement")
    public static String mul(String mul, String sign, String statement)
    {
        return "(" + mul + " " + sign + " " + statement + ")";
    }

    @ParserRule("mul -> statement")
    public static String mul(String statement)
    {
        return statement;
    }

    @ParserRule("statement -> ( add )")
    public static String parenth(String add)
    {
        return "(" + add + ")";
    }

    @ParserRule("statement -> key:variable")
    @ParserRule("statement -> key:number")
    public static String key(String key)
    {
        return key;
    }
}
