package com.christopherjung.reflectparser;

import com.christopherjung.StreamUtils;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.TDLParser;

public class MathMain
{
    public static void main(String[] args) throws Exception
    {
        TDLParser parser = new ReflectTLDGenerator().generate(MathParser.class);
        Scanner scanner = ReflectScannerGenerator.generate(MathParser.class);

        ScanResult scanResult = StreamUtils.loopFileWithResult("test.math", scanner::scan);

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
