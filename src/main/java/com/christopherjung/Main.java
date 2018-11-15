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


        Compiler compiler = new Compiler();
        compiler.addRule("start", "object  EOF");
        compiler.addRule("object", "{ } | {  members  }");
        compiler.addRule("members", "pair | pair  ,  members");
        compiler.addRule("pair", "string  :  value");
        compiler.addRule("array", "[  ] | [  elements  ]");
        compiler.addRule("elements", "value | value  ,  elements");
        compiler.addRule("value", "string | number | object | array | true | false | null");

        compiler.compile(scanResult);
    }

}
