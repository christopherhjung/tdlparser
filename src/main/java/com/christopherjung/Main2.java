package com.christopherjung;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.*;

public class Main2
{

    public static void main(String[] args)
    {
        Scanner.Builder scannerBuilder = new Scanner.Builder();

        StreamUtils.loopFile("java.scanner", scannerBuilder::addAll);

        Scanner scanner = scannerBuilder.build();
        ScanResult scanResult = StreamUtils.loopFileWithResult("test.java", scanner::scan);

        Grammar.Builder builder = new Grammar.Builder();
        StreamUtils.loopFile("java.tdl", builder::addRules);

        Grammar grammar = builder.build();

        System.out.println("container build");

        ParserTable table  = new ClosureTable().generate(grammar);

        System.out.println(TDLUtils.toString(table));

        ModifierSource source = new ModifierSource();

        source.setDefaultModifier(Modifier.CONCAT);

        TDLParser parser = new TDLParser(table, source);


        parser.parse(scanResult);
    }

}
