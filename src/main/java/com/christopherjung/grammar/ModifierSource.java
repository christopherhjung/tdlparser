package com.christopherjung.grammar;

import com.christopherjung.translator.Rule;

import java.util.HashMap;

public class ModifierSource
{
    private static final Modifier ROOT = new Modifier()
    {
        Object object;

        @Override
        public Object modify()
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
    private Modifier defaultModifier;

    public ModifierSource(HashMap<Rule, Modifier> modifiers)
    {
        this.modifiers = new HashMap<>(modifiers);
    }

    public ModifierSource()
    {
        this(new HashMap<>());
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

    public void setDefaultModifier(Modifier defaultModifier)
    {
        this.defaultModifier = defaultModifier;
    }
}

