package com.rptr1.fpg.msgs;

import com.google.gson.Gson;
import com.rptr1.fpg.util.CustomGson;

public class Message
{
    private static Gson gson = CustomGson.getGson();
    private String msgType;

    public Message(){}

    public Message( String msgType )
    {
        this.msgType = msgType;
    }

    public String toJSON()
    {
        return gson.toJson( this );
    }

    public String getMsgType()
    {
        return msgType;
    }
}
