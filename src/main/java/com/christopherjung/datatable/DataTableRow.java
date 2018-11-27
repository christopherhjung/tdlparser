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

        if(column == null)
            return;



        if (!column.getType().isAssignableFrom(obj.getClass()))
        {
            if (Number.class.isAssignableFrom(obj.getClass()))
            {
                obj = NumberUtils.cast((Number) obj, column.getType());
            }
            else
            {
                throw new RuntimeException(column.getType() + " is not Assignable from " + obj.getClass());
            }
        }


        values.put(column, obj);
    }

    public Object get(String name)
    {
        DataTableColumn column = dataTable.getColumn(name);


        return values.get(column);
    }
}
