package com.rptr1.fpg.server.main;

import com.google.gson.Gson;
import com.rptr1.fpg.error.ClientVisibleException;
import com.rptr1.fpg.game.Game;
import com.rptr1.fpg.game.LobbyManager;
import com.rptr1.fpg.msgs.*;
import com.rptr1.fpg.util.CustomGson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServerWebSocket extends WebSocketServer
{
    private final Map<InetSocketAddress, String> socketAddressToUidMap = new HashMap<>(); //connection to player id
    private final Map<String, WebSocket> uidToSocketAddressMap = new ConcurrentHashMap<>(); //player id to ws connection
    private final Map<String, String> activePlayers = new HashMap<>(); // player id to game id
    private final Map<String, Game> activeGames = new HashMap<>(); // game id to game
    private final BlockingQueue<GameEvent> gameEventQueue = new LinkedBlockingQueue<>();
    private final Gson gson = CustomGson.getGson();

    private final Map<String, MessageHandler<?>> messageHandlerMap = new HashMap<String, MessageHandler<?>>()
    {{
        put( "timeSyncRequest", new TimeSyncHandler() );
        put( "abilityTriggeredMsg", new AbilityTriggeredHandler() );
        put( "updatePlayerMovementMsg", new UpdatePlayerMovementHandler() );
    }};

    public GameServerWebSocket( InetSocketAddress address )
    {
        super( address );
        GameEventDispatcher gameEventDispatcher = new GameEventDispatcher( 3, gameEventQueue, uidToSocketAddressMap );
        gameEventDispatcher.start();
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake )
    {
//        conn.send( "Welcome to the server!" ); //This method sends a message to the new client
//        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println( "new connection to " + conn.getRemoteSocketAddress() );
        String playerUid = UUID.randomUUID().toString();
        socketAddressToUidMap.put( conn.getRemoteSocketAddress(), playerUid );
        uidToSocketAddressMap.put( playerUid, conn );
        gameEventQueue.add( new GameEvent( playerUid, new SetUidResponse( playerUid ) ) );

        String lobbyId = LobbyManager.addPlayer( playerUid );
        if(LobbyManager.getLobby( lobbyId ).getGame() == null && LobbyManager.getLobby( lobbyId ).getNumberOfPlayers() > 1 )
        {
            LobbyManager.createNewGame( lobbyId, gameEventQueue );
        }
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote )
    {
        System.out.println( "closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason );
    }

    @Override
    public void onMessage( WebSocket conn, String message )
    {
        System.out.println( "received message from " + conn.getRemoteSocketAddress() + ": " + message );
        String msgType = gson.fromJson( message, Message.class ).getMsgType();
        System.out.println( "MsgType: " + msgType );

        MessageHandler<?> handler = messageHandlerMap.get( msgType );
        if( handler != null )
        {
            try
            {
                handler.handle( message, gameEventQueue, socketAddressToUidMap.get( conn.getRemoteSocketAddress() ) );
            }
            catch( ClientVisibleException e )
            {
                e.printStackTrace();
                conn.send( new ErrorMsg( e.getMessage() ).toJSON() );
            }
        }
        else
        {
            System.out.println( "Message type unrecognized" );
        }
    }

    @Override
    public void onError( WebSocket conn, Exception ex )
    {
        System.err.println( "an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex );
    }

    @Override
    public void onStart()
    {
        System.out.println( "server started successfully" );
    }

}
