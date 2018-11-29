package com.christopherjung.examples;

import com.christopherjung.StreamUtils;
import com.christopherjung.reflectparser.ReflectScannerGenerator;
import com.christopherjung.reflectparser.ReflectTLDGenerator;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.TDLParser;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        TDLParser parser = new ReflectTLDGenerator().generate(JSONParser.class);
        Scanner scanner = ReflectScannerGenerator.generate(JSONParser.class);


        Object o = "No Data";
        long count = 0;
        long sum = 0;
        long meausurements = 100;
        try
        {
            for (; meausurements >= 0; meausurements--)
            {
                ScanJob scanJob = new ScanJob(scanner, StreamUtils.getFileStream("test.json"));
                long start = System.currentTimeMillis();
                o = parser.parse(scanJob);
                long end = System.currentTimeMillis();
                sum += end - start;
                count++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println(o);
        System.out.println(sum / count);

    }
}
