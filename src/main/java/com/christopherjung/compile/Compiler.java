package com.christopherjung.compile;

public class Compiler
{
    public void test()
    {
        test("start", "object + EOF");
        test("object", "{ + } | { + members + }");
        test("members", "pair | pair + , + members");
        test("pair", "string + : + value");
        test("array", "[ + ] | [ + elements + ]");
        test("elements", "value | value + , + elements");
        test("value", "string | number | object | array | true | false | null");


    }


    public Rule test(String a, String b)
    {
        RuleParser parser = new RuleParser();
        Rule rule = parser.parse(b);
        return rule;

    }
}
