package com.christopherjung.datatable;

public class DataTableColumn
{
    private DataTable table;
    private String name;
    private Class<?> type;

    public DataTableColumn(DataTable table, String name, Class<?> type)
    {
        this.table = table;
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        table.changeColumnName(this.name, name);
        this.name = name;
    }

    public Class<?> getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
