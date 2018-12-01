package com.christopherjung;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.scanner.ScanJob;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.*;

public class Main
{

    public static void main(String[] args)
    {
        Scanner.Builder scannerBuilder = new Scanner.Builder();
        StreamUtils.loopFile("json.scanner", scannerBuilder::addAll);
        Scanner scanner = scannerBuilder.build();
        ScanJob scanResult = StreamUtils.loopFileWithResult("test.xml", stream -> new ScanJob(scanner,stream));

        Grammar.Builder builder = new Grammar.Builder();
        StreamUtils.loopFile("json.tdl", builder::addRules);
        Grammar grammar = builder.build();

        ParserTable table = new ParserTableGenerator().generate(grammar);

        System.out.println(TDLUtils.toString(table));

        ModifierSource source = new ModifierSource();

        source.setDefaultModifier(Modifier.CONCAT);

        TDLParser parser = new TDLParser(table, source);


        Object modified =parser.parse(scanResult);


        System.out.println(modified);
    }

}
