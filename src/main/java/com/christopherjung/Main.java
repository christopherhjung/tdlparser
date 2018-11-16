package com.christopherjung;

import com.christopherjung.compile.Compiler;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Scanner;

public class Main
{

    public static void main(String[] args)
    {

        Scanner scanner = new Scanner();
        scanner.add("ignore", "\\s+");
        scanner.add("number", "[-+]?[0-9]+(.[0-9]+)?([eE][+-]?[0-9]+)?");
        scanner.add("string", "\"[^\"]*\"");
        scanner.add("null", "null");
        scanner.add("false", "false");
        scanner.add("]", "\\]");
        scanner.add("[", "\\[");
        scanner.add(":", "\\:");
        scanner.add(",", "\\,");
        scanner.add("}", "\\}");
        scanner.add("{", "\\{");

        ScanResult scanResult = StreamUtils.loopFile("json2", scanner::scan);

        Compiler.Builder builder = new Compiler.Builder();
        builder.addRootRule("object EOF");
        builder.addRule("object", "{ }");
        builder.addRule("object", "{ members }");
        builder.addRule("members", "pair");
        builder.addRule("members", "pair , members");
        builder.addRule("pair", "string : value");
        builder.addRule("array", "[ ]");
        builder.addRule("array", "[ elements ]");
        builder.addRule("elements", "value");
        builder.addRule("elements", "value , elements");
        builder.addRule("value", "string");
        builder.addRule("value", "number");
        builder.addRule("value", "object");
        builder.addRule("value", "array");
        builder.addRule("value", "true");
        builder.addRule("value", "false");
        builder.addRule("value", "null");

        Compiler compiler = builder.build();
        compiler.compile(scanResult);
    }

}
