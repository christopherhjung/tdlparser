package com.christopherjung.grammar;

import com.christopherjung.translator.Rule;

import java.util.HashMap;

public class ModifierSource
{
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
            modifier = defaultModifier;
        }

        return modifier;
    }

    public void setDefaultModifier(Modifier defaultModifier)
    {
        this.defaultModifier = defaultModifier;
    }
}

