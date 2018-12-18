package com.christopherjung.grammar;

import com.christopherjung.translator.Rule;

import java.util.HashMap;
import java.util.function.Supplier;

public class ModifierSource
{
    private static final Modifier ROOT = new Modifier()
    {
        Object object;

        @Override
        public Object modify(Object parser)
        {
            return object;
        }

        @Override
        public void register(int index, Object obj)
        {
            object = obj;
        }
    };

    private HashMap<Rule, Modifier> modifiers;
    private Supplier<Object> parserSupplier;

    public ModifierSource(HashMap<Rule, Modifier> modifiers, Supplier<Object> parserSupplier)
    {
        this.parserSupplier = parserSupplier;
        this.modifiers = new HashMap<>(modifiers);
    }

    public Object createTag()
    {
        return parserSupplier.get();
    }

    public Modifier getModifier(Rule rule)
    {
        Modifier modifier = modifiers.get(rule);

        if (modifier == null)
        {
            modifier = ROOT;
        }

        return modifier;
    }
}

