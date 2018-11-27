package com.christopherjung.reflectparser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface Nodes
{
    Node[] value();
}
