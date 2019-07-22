package com.christopherjung;

import com.christopherjung.examples.JSONParser;
import com.christopherjung.examples.JSONParser2;
import com.christopherjung.parser.Parser;
import com.christopherjung.reflectparser.ReflectScannerGenerator;
import com.christopherjung.reflectparser.ReflectTLDGenerator;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.utils.StreamUtils;

import org.json.*;


import java.io.ByteArrayInputStream;

public class Main {
    public static void main(String[] args) {
        Parser parser = ReflectTLDGenerator.generate(JSONParser2.class);
        Scanner scanner = ReflectScannerGenerator.generate(JSONParser2.class);



        long start = System.currentTimeMillis();
        for(int i = 0 ; i < 100 ; i++){
            ScanJob job = new ScanJob(scanner, StreamUtils.getFileStream("test.json"));
            Object object = parser.parse(job);

            //JSONArray obj = new JSONArray(StreamUtils.getString("test.json"));
        }

        long end = System.currentTimeMillis();

        System.out.println(end - start);

        //System.out.println(object);
    }
}
