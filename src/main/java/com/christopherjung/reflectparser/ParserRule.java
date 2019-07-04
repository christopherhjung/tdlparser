package com.christopherjung.reflectparser;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Nodes.class)
public @interface ParserRule
{
    String value();
}
