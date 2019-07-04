package com.christopherjung.datatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class DataTable
{
    private List<DataTableRow> rows;
    private LinkedHashMap<String, DataTableColumn> columns;

    public DataTable()
    {
        rows = new ArrayList<>();
        columns = new LinkedHashMap<>();
    }

    public DataTableRow newRow()
    {
        return new DataTableRow(this);
    }

    public Collection<DataTableColumn> columns()
    {
        return columns.values();
    }

    public DataTableColumn getColumn(String name)
    {
        return columns.get(name);
    }

    void changeColumnName(String old, String name)
    {
        columns.put(name, columns.remove(old));
    }

    public DataTableColumn addColumn(String name, Class<?> type)
    {
        DataTableColumn column = new DataTableColumn(this, name, type);

        columns.put(name, column);

        return column;
    }

    public void addRow(DataTableRow row)
    {
        rows.add(row);
    }

    @Override
    public String toString()
    {
        boolean equaly = false;

        int[] lengths = new int[columns.size()];
        String[] headers = new String[columns.size()];
        String[][] values = new String[rows.size()][columns.size()];

        int index = 0;
        for (String name : columns.keySet())
        {
            headers[index] = name;
            lengths[index] = Math.max(lengths[index], name.length());
            index++;
        }

        int rowIndex = 0;
        for (DataTableRow row : rows)
        {
            int colIndex = 0;
            for (String name : columns.keySet())
            {
                Object obj = row.get(name);
                String value = "";

                if (obj != null)
                {
                    value = obj.toString();
                }

                values[rowIndex][colIndex] = value;
                lengths[colIndex] = Math.max(lengths[colIndex], value.length());
                colIndex++;
            }
            rowIndex++;
        }

        if (equaly)
        {
            int max = 0;

            for (int length : lengths)
            {
                max = Math.max(max, length);
            }

            for (int i = 0; i < lengths.length; i++)
            {
                lengths[i] = max;
            }
        }


        StringBuilder builder = new StringBuilder();


        for (int i = 0; i < lengths.length; i++)
        {
            for (int j = lengths[i]; j >= 0; j--)
            {
                builder.append('_');
            }
        }

        builder.append("_\n");

        for (int col = 0; col < headers.length; col++)
        {
            builder.append('|');
            builder.append(headers[col]);
            for (int i = lengths[col] - headers[col].length(); i > 0; i--)
            {
                builder.append(' ');
            }
        }
        builder.append("|\n");


        builder.append('|');
        for (int i = 0; i < lengths.length; i++)
        {
            if (i > 0)
            {
                builder.append('-');
            }
            for (int j = lengths[i]; j > 0; j--)
            {
                builder.append('-');
            }
        }
        builder.append("|\n");

        for (int row = 0; row < values.length; row++)
        {
            for (int col = 0; col < values[row].length; col++)
            {
                builder.append('|');
                builder.append(values[row][col]);
                for (int i = lengths[col] - values[row][col].length(); i > 0; i--)
                {
                    builder.append(' ');
                }
            }
            builder.append("|\n");
        }

        for (int i = 0; i < lengths.length; i++)
        {
            for (int j = lengths[i]; j > 0; j--)
            {
                builder.append("̅");
            }
            builder.append("̅");
        }
        builder.append("̅\n");


        return builder.toString();
    }
}
