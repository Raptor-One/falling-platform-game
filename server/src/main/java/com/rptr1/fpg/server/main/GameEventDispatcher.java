package com.rptr1.fpg.server.main;

import com.rptr1.fpg.msgs.GameEvent;
import com.rptr1.fpg.msgs.Message;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class GameEventDispatcher
{
    private BlockingQueue<GameEvent> gameEventQueue;
    private Map<String, WebSocket> uidToSocketAddressMap;
    private List<Thread> threads = new ArrayList<>();

    public GameEventDispatcher( int numOfThreads, BlockingQueue<GameEvent> gameEventQueue, Map<String, WebSocket> uidToSocketAddressMap )
    {
        this.gameEventQueue = gameEventQueue;
        this.uidToSocketAddressMap = uidToSocketAddressMap;
        for( int i = 0; i < numOfThreads; i++ )
        {
            threads.add( new Thread( this::sendMessagesProcess ) );
        }
    }

    public void start()
    {
        for( Thread thread : threads )
        {
            thread.start();
        }
    }

    public void stop()
    {
        try
        {
            gameEventQueue.clear();
            for( int i = 0; i < threads.size(); i++ )
            {
                gameEventQueue.put( new GameEvent( true ) );
            }
            for( Thread thread : threads )
            {
                thread.join();
            }
        }
        catch( InterruptedException e )
        {
            e.printStackTrace();
        }
    }

    private void sendMessagesProcess()
    {
        while( true )
        {
            try
            {
                GameEvent gameEvent = gameEventQueue.take();
                if( gameEvent.isKillThread() ) return;
                for( String playerUid : gameEvent.getRecipients() )
                {
                    WebSocket conn = uidToSocketAddressMap.get( playerUid );
                    for( Message msg : gameEvent.getMessages() )
                    {
                        conn.send( msg.toJSON() );
                    }
                }
            }
            catch( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
    }
}
