package com.christopherjung.compile;

import com.christopherjung.regex.TreeNode;

public class TokenNode extends TreeNode
{
    private String value;

    public TokenNode(String value)
    {
        this.value = value;
        setNullable(false);

        //addFirstPosition(this);
        //addLastPosition(this);
    }

    @Override
    protected void toRegEx(StringBuilder sb)
    {

    }

    @Override
    public TreeNode clone()
    {
        return null;
    }
}
