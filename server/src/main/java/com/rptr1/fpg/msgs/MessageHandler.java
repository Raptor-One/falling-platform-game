package com.rptr1.fpg.msgs;

import com.google.gson.Gson;
import com.rptr1.fpg.util.CustomGson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public abstract class MessageHandler<T>
{
    private final Gson gson = CustomGson.getGson();

    public void handle( String msg, String playerUid )
    {
        Type mySuperclass = this.getClass().getGenericSuperclass();
        Type tType = ((ParameterizedType)mySuperclass).getActualTypeArguments()[0];
        handle( gson.<T>fromJson( msg, tType), playerUid );
    }

    public abstract void handle( T msg, String playerUid );
}
