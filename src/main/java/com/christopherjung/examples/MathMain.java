package com.christopherjung.examples;

import com.christopherjung.StreamUtils;
import com.christopherjung.examples.MathParser;
import com.christopherjung.reflectparser.ReflectScannerGenerator;
import com.christopherjung.reflectparser.ReflectTLDGenerator;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.TDLParser;

public class MathMain
{
    public static void main(String[] args) throws Exception
    {
        TDLParser parser = new ReflectTLDGenerator().generate(MathParser.class);
        Scanner scanner = ReflectScannerGenerator.generate(MathParser.class);

        ScanJob scanResult = StreamUtils.loopFileWithResult("test.xml", stream -> new ScanJob(scanner,stream));

        Object o = "No Data";
        try
        {
            o = parser.parse(scanResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("-----------------------");
        System.out.println(scanResult);
        System.out.println(o);

    }
}
