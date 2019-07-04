package com.christopherjung.reflectparser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ScannerToken
{
    String lookahead() default "";
    String value() default "";
}
