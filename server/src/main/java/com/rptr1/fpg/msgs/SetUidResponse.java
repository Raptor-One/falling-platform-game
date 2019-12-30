package com.rptr1.fpg.msgs;

public class SetUidResponse extends Message
{
    private String uid;

    public SetUidResponse( String uid )
    {
        super("setUidResponse");
        this.uid = uid;
    }
}
