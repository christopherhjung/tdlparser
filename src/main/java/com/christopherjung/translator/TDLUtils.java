package com.christopherjung.translator;

import com.christopherjung.datatable.DataTable;
import com.christopherjung.datatable.DataTableRow;
import com.christopherjung.grammar.Grammar;

public class TDLUtils
{
    private TDLUtils()
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

            if (test.getRestoreAction() != -1)
            {
                if (test.getRestoreAction() == 0)
                {
                    row.set("EOF", "r" + test.getRestoreAction());
                }
                else
                {
                    for (String symbol : grammar.getAlphabet())
                    {
                        row.set(symbol, "r" + test.getRestoreAction());
                    }
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
                row.set(entry.getKey(), entry.getValue());
            }

            dataTable.addRow(row);
        }

        return dataTable.toString();
    }
}
