package com.christopherjung.reflectparser;

import java.util.ArrayList;
import java.util.List;

public class JSONParser
{
    @ScannerStructure
    public static String structureChars = "[]{},:";

    @ScannerIgnore
    public static String ignore = "\\s+";

    @ScannerToken
    public static String number = "[-+]?[0-9]+(.[0-9]+)?([eE][+-]?[0-9]+)?";

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

        public JSONObject()
        {
        }

        public JSONObject(List<JSONPair> pairs)
        {
            this.pairs = pairs;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            for (JSONPair pair : pairs)
            {
                if (sb.length() > 1)
                {
                    sb.append(',');
                }
                sb.append(pair);
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

    @ParserRoot("object EOF")
    public static JSONObject start(JSONObject object)
    {
        return object;
    }

    @ParserRule("object -> { }")
    public static JSONObject emptyObject()
    {
        return new JSONObject();
    }

    @ParserRule("object -> { members }")
    public static JSONObject nonEmptyObject(List<JSONPair> members)
    {
        return new JSONObject(members);
    }

    @ParserRule("members -> pair")
    public static List<JSONPair> singlePair(JSONPair pair)
    {
        List<JSONPair> pairs = new ArrayList<>();
        pairs.add(pair);
        return pairs;
    }

    @ParserRule("members -> members , pair")
    public static List<JSONPair> multiplePairs(JSONPair pair, List<JSONPair> members)
    {
        members.add(pair);
        return members;
    }

    @ParserRule("pair -> key:string : value")
    public static JSONPair pair(String key, JSONValue value)
    {
        return new JSONPair(key, value);
    }

    @ParserRule("array -> [ ]")
    public static JSONList emptyArray()
    {
        return new JSONList();
    }

    @ParserRule("array -> [ elements ]")
    public static JSONList nonEmptyArray(List<JSONValue> elements)
    {
        return new JSONList(elements);
    }

    @ParserRule("elements -> value")
    public static List<JSONValue> singleValue(JSONValue value)
    {
        List<JSONValue> values = new ArrayList<>();
        values.add(value);
        return values;
    }

    @ParserRule("elements -> elements , value")
    public static List<JSONValue> multipleValues(JSONValue value, List<JSONValue> elements)
    {
        elements.add(value);
        return elements;
    }

    @ParserRule("value -> string")
    public static JSONString stringValue(String string)
    {
        return new JSONString(string);
    }

    @ParserRule("value -> number")
    public static JSONNumber numberValue(String number)
    {
        return new JSONNumber(Double.parseDouble(number));
    }

    @ParserRule("value -> object")
    public static JSONObject objectValue(JSONObject object)
    {
        return object;
    }

    @ParserRule("value -> array")
    public static JSONList arrayValue(JSONList array)
    {
        return array;
    }

    @ParserRule("value -> bool")
    public static JSONBoolean boolValue(boolean bool)
    {
        return new JSONBoolean(bool);
    }

    @ParserRule("value -> null")
    public static JSONValue nullValue()
    {
        return new JSONValue();
    }
}
