package com.rptr1.fpg.msgs;

public class ErrorMsg extends Message
{
    private String error;

    public ErrorMsg( String error )
    {
        super( "errorMsg" );
        this.error = error;
    }
}
