package com.christopherjung.translator;

public class TLDParseException extends RuntimeException
{
    public TLDParseException()
    {
    }

    public TLDParseException(String message)
    {
        super(message);
    }

    public TLDParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TLDParseException(Throwable cause)
    {
        super(cause);
    }

    public TLDParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
