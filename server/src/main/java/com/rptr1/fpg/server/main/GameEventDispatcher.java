package com.rptr1.fpg.server.main;

import com.rptr1.fpg.msgs.GameEvent;
import com.rptr1.fpg.msgs.Message;
import org.java_websocket.WebSocket;


public class GameEventDispatcher
{
    public static void dispatch( GameEvent gameEvent)
    {
        for( String playerUid : gameEvent.getRecipients() )
        {
            WebSocket conn = GameServerWebSocket.uidToSocketAddressMap.get( playerUid );
            for( Message msg : gameEvent.getMessages() )
            {
                conn.send( msg.toJSON() );
            }
        }
    }

}
