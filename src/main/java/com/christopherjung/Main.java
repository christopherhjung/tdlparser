package com.christopherjung;

import com.christopherjung.datatable.DataTable;
import com.christopherjung.datatable.DataTableRow;
import com.christopherjung.scanner.ScanResult;
import com.christopherjung.scanner.Scanner;
import com.christopherjung.translator.ClosureTable;
import com.christopherjung.translator.Grammar;
import com.christopherjung.translator.ParserTable;
import com.christopherjung.translator.TDLParser;

public class Main
{

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner();
        scanner.add("ignore", "\\s+");
        scanner.add("number", "[-+]?[0-9]+(.[0-9]+)?([eE][+-]?[0-9]+)?");
        scanner.add("string", "\"[^\"]*\"");
        scanner.add("null", "null");
        scanner.add("false", "false");
        scanner.add("]", "\\]");
        scanner.add("[", "\\[");
        scanner.add(":", "\\:");
        scanner.add(",", "\\,");
        scanner.add("}", "\\}");
        scanner.add("{", "\\{");

        ScanResult scanResult = StreamUtils.loopFileWithResult("test.json", scanner::scan);

        Grammar.Builder builder = new Grammar.Builder();

        StreamUtils.loopFile("json.tdl", builder::addRules);

        Grammar grammar = builder.build();

        ClosureTable closureTable = new ClosureTable(grammar);

        ParserTable table = closureTable.getTable();

        DataTable dataTable = new DataTable();
        dataTable.addColumn("state", Integer.class);

        for (String symbol : grammar.getAlphabet())
        {
            dataTable.addColumn(symbol, String.class);
        }

        dataTable.addColumn("$", String.class);
        for (String name : grammar.getRuleNames())
        {
            dataTable.addColumn(name, String.class);
        }
        dataTable.addColumn("restore", String.class);

        int i = 0;
        for (ParserTable.Entry test : table.getEntries())
        {
            DataTableRow row = dataTable.newRow();

            if (test.getRestoreActions() != -1)
            {
                if (test.getRestoreActions() == 0)
                {
                    row.set("$", "acc");
                }
                else
                {
                    for (String symbol : grammar.getAlphabet())
                    {
                        row.set(symbol, "r" + test.getRestoreActions());
                    }
                    //row.set("$", "r" + test.getRestoreActions());
                }

                row.set("restore", test.getRule().toString());
            }


            row.set("state", i++);

            for (var entry : test.getActions().entrySet())
            {
                row.set(entry.getKey(), "s" + entry.getValue());
            }

            for (var entry : test.getGoTos().entrySet())
            {
                row.set(entry.getKey(), entry.getValue().toString());
            }

            dataTable.addRow(row);
        }


        System.out.println(dataTable);

        TDLParser parser = new TDLParser(table);

        parser.test(scanResult);
    }

}
