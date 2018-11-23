package com.christopherjung.datatable;

import java.util.HashMap;

public class DataTableRow
{
    private DataTable dataTable;
    private HashMap<DataTableColumn, Object> values;

    public DataTableRow(DataTable dataTable)
    {
        this.dataTable = dataTable;
        values = new HashMap<>();
    }

    public void set(String name, Object obj)
    {
        DataTableColumn column = dataTable.getColumn(name);

        if (!column.getType().isAssignableFrom(obj.getClass()))
        {
            throw new RuntimeException("test");
        }

        values.put(column, obj);
    }

    public Object get(String name)
    {
        DataTableColumn column = dataTable.getColumn(name);


        return values.get(column);
    }
}
