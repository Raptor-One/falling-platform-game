package com.rptr1.fpg.msgs;

import com.rptr1.fpg.server.main.GameEventDispatcher;

public class TimeSyncHandler extends MessageHandler<TimeSyncRequest>
{

    @Override
    public void handle( TimeSyncRequest msg, String playerUid )
    {

        System.out.println( "Connection has estimated timeOffset of " + (System.currentTimeMillis() - msg.getTimestamp() ));
        GameEventDispatcher.dispatch( new GameEvent( playerUid, new TimeSyncResponse( msg.getTimestamp(), System.currentTimeMillis() ) ) );
    }
}
