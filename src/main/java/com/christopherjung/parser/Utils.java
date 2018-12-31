package com.christopherjung.parser;

import com.christopherjung.datatable.DataTable;
import com.christopherjung.datatable.DataTableRow;
import com.christopherjung.grammar.Grammar;

import java.util.HashSet;
import java.util.Set;

public class Utils
{
    private Utils()
    {

    }

    public static String toString(ParserTable table)
    {
        Grammar grammar = table.getGrammar();

        DataTable dataTable = new DataTable();
        dataTable.addColumn("state", Integer.class);

        for (String symbol : grammar.getAlphabet())
        {
            dataTable.addColumn(symbol, String.class);
        }

        for (String name : grammar.getRuleNames())
        {
            dataTable.addColumn(name, Integer.class);
        }
        dataTable.addColumn("restore", String.class);

        int i = 0;
        for (ParserTable.Entry test : table.getEntries())
        {
            DataTableRow row = dataTable.newRow();

            for (String symbol : grammar.getAlphabet())
            {
                if (table.isIgnore(symbol))
                {
                    row.set(symbol, "igno");
                }
            }

            row.set("restore", "");

            Set<Rule> visited = new HashSet<>();

            for (var entry : test.getRestoreRules().entrySet())
            {
                row.set(entry.getKey(), "r" + entry.getValue().getId());

                if (!visited.contains(entry.getValue()))
                {
                    row.set("restore", row.get("restore") + " ; " + entry.getValue());
                }

                visited.add(entry.getValue());
            }

            row.set("state", i++);

            for (var entry : test.getActions().entrySet())
            {
                row.set(entry.getKey(), "s" + entry.getValue());
            }

            for (var entry : test.getGoTos().entrySet())
            {
                row.set(entry.getKey(), entry.getValue());
            }

            dataTable.addRow(row);
        }

        return dataTable.toString();
    }
}
