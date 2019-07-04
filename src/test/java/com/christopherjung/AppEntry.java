package com.christopherjung;

import static org.junit.Assert.assertTrue;

import com.christopherjung.examples.JSONParser;
import com.christopherjung.parser.Parser;
import com.christopherjung.reflectparser.ReflectScannerGenerator;
import com.christopherjung.reflectparser.ReflectTLDGenerator;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Scanner;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Unit fetch for simple App.
 */
public class AppEntry
{
    /**
     * Rigorous Entry :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        Parser parser = ReflectTLDGenerator.generate(JSONParser.class);
        Scanner scanner = ReflectScannerGenerator.generate(JSONParser.class);
        ScanJob job = new ScanJob(scanner, new ByteArrayInputStream("sasasasA".getBytes()));

        Object object = parser.parse(job);

        System.out.println(object);
    }
}
