package com.christopherjung.reflectparser;

import java.util.ArrayList;
import java.util.List;

public class XMLParser
{
    @ScannerStructure
    public static String structureChars = "</>=.";

    @ScannerToken
    public static String word = "\\w";

    @ScannerToken
    public static String digit = "\\d";

    @ScannerToken
    public static String string = "\"[^\"]+\"";

    @ParserIgnore
    @ScannerToken
    public static String space = "\\s+";

    @ScannerToken
    public static String rest = ".";

    static class XMLAttribute
    {
        private String key;
        private String value;

        public XMLAttribute(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return key + "=" + value + "";
        }
    }

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

        @Override
        public String toString()
        {
            return value;
        }
    }

    static class XMLNode extends XMLElement
    {
        private String name;
        private List<XMLElement> children;
        private List<XMLAttribute> attributes;

        public XMLNode(String name, List<XMLElement> children)
        {
            this.name = name;
            this.children = new ArrayList<>(children);
            this.attributes = new ArrayList<>();
        }

        public XMLNode(String name, List<XMLAttribute> attributes, List<XMLElement> children)
        {
            this.name = name;
            this.children = attributes == null ? new ArrayList<>() : children;
            this.attributes = attributes == null ? new ArrayList<>() : attributes;
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

            for (XMLAttribute attribute : attributes)
            {
                sb.append(' ');
                sb.append(attribute.toString());
            }

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

    @Node("node -> < name space attributes > elements < / name >")
    public static XMLNode nonEmptyNode(String name, List<XMLAttribute> attributes, List<XMLElement> elements)
    {
        return new XMLNode(name, attributes, elements);
    }

    @Node("element -> text")
    public static XMLElement nonEmptyNode(String text)
    {
        return new XMLText(text);
    }

    @Node("node -> < name space attributes > < / name >")
    @Node("node -> < name space attributes / >")
    public static XMLNode emptyNode(String name, List<XMLAttribute> attributes)
    {
        return new XMLNode(name, attributes, null);
    }

    @Node("node -> < name > < / name >")
    @Node("node -> < name / >")
    public static XMLNode emptyNode(String name)
    {
        return new XMLNode(name);
    }

    @Node("element -> node")
    public static XMLElement emptyNode(XMLNode node)
    {
        return node;
    }

    @Node("attributes -> attribute")
    public static List<XMLAttribute> attrib(XMLAttribute attribute)
    {
        List<XMLAttribute> attributes = new ArrayList<>();
        attributes.add(attribute);

        return attributes;
    }

    @Node("attributes -> attributes space attribute")
    public static List<XMLAttribute> attrib(XMLAttribute attribute, List<XMLAttribute> attributes)
    {
        attributes.add(attribute);
        return attributes;
    }

    @Node("attribute -> key:name = value:string ")
    public static XMLAttribute attrib(String key, String value)
    {
        return new XMLAttribute(key, value);
    }

    @Node("elements -> elements element")
    public static List<XMLElement> multiNode(XMLElement element, List<XMLElement> elements)
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
    public static String text(StringBuilder textBuilder)
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
    @Node("sign -> sign:space")
    public static String text233(String sign)
    {
        return sign;
    }
    //######################################TEXT
}
