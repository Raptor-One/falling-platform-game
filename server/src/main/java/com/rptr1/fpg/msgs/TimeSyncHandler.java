package com.rptr1.fpg.msgs;

import java.util.concurrent.BlockingQueue;

public class TimeSyncHandler extends MessageHandler<TimeSyncRequest>
{

    @Override
    public void handle( TimeSyncRequest msg, BlockingQueue<GameEvent> gameEventQueue, String playerUid )
    {
        gameEventQueue.add( new GameEvent( playerUid, new TimeSyncResponse( msg.getTimestamp(), System.currentTimeMillis() ) ) );
    }
}
