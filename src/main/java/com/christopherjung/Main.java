package com.christopherjung;

import com.christopherjung.machine.Machine;
import com.christopherjung.machine.Pattern;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.scanner.Token;

import javax.crypto.Mac;
import java.io.*;
import java.util.List;

public class Main
{

    public static void main(String[] args) throws IOException
    {
       // InputStream stream = new ByteArrayInputStream("azuubuu".getBytes());

        File file = new File(Main.class.getClassLoader().getResource("json").getFile());


        long start2 = System.nanoTime();

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

        long end2 = System.nanoTime();

        System.out.println("Compile: " + (end2 - start2)/1000000.0);


        InputStream stream = new FileInputStream(file);
        List<Scanner.ScanResult> scan = scanner.scan(stream);
        stream.close();

        System.out.println(scan);
    }

}
