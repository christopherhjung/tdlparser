package com.christopherjung;

import com.christopherjung.grammar.Grammar;
import com.christopherjung.grammar.Modifier;
import com.christopherjung.grammar.ModifierSource;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.ClosureTable;
import com.christopherjung.translator.ParserTable;
import com.christopherjung.translator.TDLParser;
import com.christopherjung.translator.TDLUtils;

public class NullableMain
{

    public static void main(String[] args)
    {

        Grammar.Builder builder = new Grammar.Builder();
        StreamUtils.loopFile("nullable.tdl", builder::addRules);
        Grammar grammar = builder.build();

        ParserTable table  = new ClosureTable().generate(grammar);

        ModifierSource source = new ModifierSource();
        source.setDefaultModifier(Modifier.CONCAT);

        TDLParser parser = new TDLParser(table, source);

        Scanner.Builder scannerBuilder = new Scanner.Builder();
        StreamUtils.loopFile("nullable.scanner", scannerBuilder::addAll);
        Scanner scanner = scannerBuilder.build();
        ScanResult scanResult = StreamUtils.loopFileWithResult("nullable.test", scanner::scan);

        System.out.println(TDLUtils.toString(table));
        System.out.println(scanResult);
        System.out.println(parser.parse(scanResult));
    }

}
