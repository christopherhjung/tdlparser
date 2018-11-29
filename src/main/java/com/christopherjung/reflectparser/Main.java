package com.christopherjung.reflectparser;

import com.christopherjung.StreamUtils;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.TDLParser;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        TDLParser parser = new ReflectTLDGenerator().generate(XMLParser2.class);
        Scanner scanner = ReflectScannerGenerator.generate(XMLParser2.class);

        ScanResult scanResult = StreamUtils.loopFileWithResult("test.xml", scanner::scan);

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
