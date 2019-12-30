package com.rptr1.fpg.msgs;

import com.google.gson.Gson;
import com.rptr1.fpg.util.CustomGson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public abstract class MessageHandler<T>
{
    private final Gson gson = CustomGson.getGson();

    public void handle( String msg, BlockingQueue<GameEvent> gameEventQueue, String playerUid )
    {
        Type mySuperclass = this.getClass().getGenericSuperclass();
        Type tType = ((ParameterizedType)mySuperclass).getActualTypeArguments()[0];
        handle( gson.<T>fromJson( msg, tType), gameEventQueue, playerUid );
    }

    public abstract void handle( T msg, BlockingQueue<GameEvent> gameEventQueue, String playerUid );
}
