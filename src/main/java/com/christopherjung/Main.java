package com.christopherjung;

import com.christopherjung.examples.JSONParser;
import com.christopherjung.parser.Parser;
import com.christopherjung.reflectparser.ReflectScannerGenerator;
import com.christopherjung.reflectparser.ReflectTLDGenerator;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Scanner;

import java.io.ByteArrayInputStream;

public class Main {
    public static void main(String[] args) {
        Parser parser = ReflectTLDGenerator.generate(JSONParser.class);
        Scanner scanner = ReflectScannerGenerator.generate(JSONParser.class);
        ScanJob job = new ScanJob(scanner, new ByteArrayInputStream("sasasasA".getBytes()));

        Object object = parser.parse(job);

        System.out.println(object);
    }
}
