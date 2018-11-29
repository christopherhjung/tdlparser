package com.christopherjung.reflectparser;

import com.christopherjung.StreamUtils;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.TDLParser;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        TDLParser parser = new ReflectTLDGenerator().generate(XMLParser2.class);
        Scanner scanner = ReflectScannerGenerator.generate(XMLParser2.class);

        ScanJob scanJob = new ScanJob(scanner, StreamUtils.getFileStream("test.xml"));

        Object o = "No Data";
        try
        {
            o = parser.parse(scanJob);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("-----------------------");
        System.out.println(scanJob);
        System.out.println(o);

    }
}
