package com.rptr1.fpg.msgs;

public class RemovePlayerResponse extends Message
{
    private String uid;

    public RemovePlayerResponse( String uid )
    {
        super("removePlayerResponse");
        this.uid = uid;
    }
}
