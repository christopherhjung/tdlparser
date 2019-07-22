package com.christopherjung.examples;

import com.christopherjung.reflectparser.ParserRule;
import com.christopherjung.reflectparser.ScannerIgnore;
import com.christopherjung.reflectparser.ScannerSingle;
import com.christopherjung.reflectparser.ScannerToken;

import java.util.List;

public class JSONParser2
{
    @ScannerSingle
    public static String structureChars = "[]{},:";

    @ScannerIgnore
    public static String ignore = "\\s+";

    @ScannerToken
    public static String number = "[-+]?[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?";

    @ScannerToken
    public static String string = "\"[^\"]*\"";

    @ScannerToken
    public static String bool = "false|true";


    public static class JSONValue
    {

    }

    public static class JSONPair
    {
        String key;
        JSONValue value;

        public JSONPair(String key, JSONValue value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return key + ":" + value;
        }
    }

    public static class JSONList extends JSONValue
    {
        List<JSONValue> values;

        public JSONList()
        {
        }

        public JSONList(List<JSONValue> values)
        {
            this.values = values;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (JSONValue value : values)
            {
                if (sb.length() > 1)
                {
                    sb.append(',');
                }
                sb.append(value);
            }
            sb.append(']');
            return sb.toString();
        }
    }

    public static class JSONObject extends JSONValue
    {
        List<JSONPair> pairs;


        public JSONObject(List<JSONPair> pairs)
        {
            this.pairs = pairs;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            if(pairs != null){
                for (JSONPair pair : pairs)
                {
                    if (sb.length() > 1)
                    {
                        sb.append(',');
                    }
                    sb.append(pair);
                }
            }
            sb.append('}');
            return sb.toString();
        }
    }

    public static class JSONString extends JSONValue
    {
        String value;

        public JSONString(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }
    }

    public static class JSONNumber extends JSONValue
    {
        double value;

        public JSONNumber(double value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value + "";
        }
    }

    public static class JSONBoolean extends JSONValue
    {
        boolean value;

        public JSONBoolean(boolean value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value + "";
        }
    }

    @ParserRule("root -> value")
    public static JSONValue start(JSONValue object)
    {
        return object;
    }

    @ParserRule("object -> '{' pair[','] '}'")
    public static JSONObject nonEmptyObject(List<JSONPair> members)
    {
        return new JSONObject(members);
    }

    @ParserRule("pair -> string ':' value")
    public static JSONPair pair(String key, JSONValue value)
    {
        return new JSONPair(key, value);
    }

    @ParserRule("array -> '[' value[','] ']'")
    public static JSONList nonEmptyArray(List<JSONValue> elements)
    {
        return new JSONList(elements);
    }

    @ParserRule("value -> object")
    public static JSONValue objectValue(JSONObject object)
    {
        return object;
    }

    @ParserRule("value -> array")
    public static JSONValue arrayValue(JSONList array)
    {
        return array;
    }

    @ParserRule("value -> string")
    public static JSONString stringValue(String string)
    {
        return new JSONString(string);
    }

    @ParserRule("value -> number")
    public static JSONValue numberValue(String number)
    {
        return new JSONNumber(Double.parseDouble(number));
    }

    @ParserRule("value -> bool")
    public static JSONValue boolValue(String bool)
    {
        return new JSONBoolean(Boolean.parseBoolean(bool));
    }

    @ParserRule("value -> null")
    public static JSONValue nullValue()
    {
        return new JSONValue();
    }
}
