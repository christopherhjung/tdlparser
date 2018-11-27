package com.christopherjung.reflectparser;

import java.util.ArrayList;
import java.util.List;

public class XMLParser
{
    @ScannerStructure
    public static String structureChars = "</>=.";

    @ScannerIgnore
    public static String ignore = "\\s+";

    @ScannerToken
    public static String word = "\\w";

    @ScannerToken
    public static String digit = "\\d";

    @ScannerToken
    public static String rest = ".";

    static class XMLElement
    {

    }

    static class XMLText extends XMLElement
    {
        String value;

        public XMLText(String value)
        {
            this.value = value;
        }
    }

    static class XMLNode extends XMLElement
    {
        private String name;
        private List<XMLElement> children;

        public XMLNode(String name, List<XMLElement> children)
        {
            this.name = name;
            this.children = new ArrayList<>(children);
        }

        public XMLNode(String name)
        {
            this(name, new ArrayList<>());
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();

            sb.append('<');
            sb.append(name);
            sb.append('>');

            for (Object node : children)
            {
                sb.append(node.toString());
            }

            sb.append("</");
            sb.append(name);
            sb.append('>');

            return sb.toString();
        }
    }

    @RootNode("node EOF")
    public static XMLElement start(XMLElement node)
    {
        return node;
    }

    @Node("node -> < name > elements < / name >")
    public static XMLNode nonEmptyNode(String name, List<XMLElement> elements)
    {
        return new XMLNode(name, elements);
    }

    @Node("node -> text")
    public static XMLElement nonEmptyNode(String text)
    {
        return new XMLText(text);
    }

    @Node("element -> < name > < / name >")
    @Node("element -> < name / >")
    public static XMLNode emptyNode(String name)
    {
        return new XMLNode(name);
    }

    @Node("elements -> elements element")
    public static List<XMLElement> multiNode(XMLNode element, List<XMLElement> elements)
    {
        elements.add(element);
        return elements;
    }

    @Node("elements -> element")
    public static List<XMLElement> multiNode(XMLElement element)
    {
        List<XMLElement> nodes = new ArrayList<>();
        nodes.add(element);
        return nodes;
    }

    //######################################NAME
    @Node("name -> nameBuilder")
    public static String nameWithDigits(Object nameBuilder)
    {
        return nameBuilder.toString();
    }

    @Node("nameBuilder -> sign:word nameWithDigits")
    @Node("nameWithDigits -> sign:wordOrDigit nameWithDigits")
    public static StringBuilder nameWithDigits(Object sign, StringBuilder nameWithDigits)
    {
        nameWithDigits.insert(0, sign.toString());
        return nameWithDigits;
    }

    @Node("nameBuilder -> sign:word")
    @Node("nameWithDigits -> sign:wordOrDigit")
    public static StringBuilder nameWithDigits(String sign)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(sign);
        return sb;
    }

    @Node("wordOrDigit -> sign:digit")
    @Node("wordOrDigit -> sign:word")
    public static String wordOrDigit(String sign)
    {
        return sign;
    }
    //######################################NAME

    //######################################TEXT
    @Node("text -> textBuilder")
    public static String text(Object textBuilder)
    {
        return textBuilder.toString();
    }

    @Node("textBuilder -> textBuilder sign")
    public static StringBuilder text(Object sign, StringBuilder textBuilder)
    {
        textBuilder.append(sign.toString());
        return textBuilder;
    }

    @Node("textBuilder -> sign")
    public static StringBuilder text(String sign)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(sign);
        return sb;
    }

    @Node("sign -> sign:digit")
    @Node("sign -> sign:word")
    @Node("sign -> sign:rest")
    public static String text233(String sign)
    {
        return sign;
    }
    //######################################TEXT
}
