package com.rptr1.fpg.game;

import com.rptr1.fpg.error.ClientVisibleException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager
{
    private static Map<String, Lobby> lobbyMap = new ConcurrentHashMap<>();
    private static Map<String, String> playerToLobbyMap = new ConcurrentHashMap<>();

    public static String createLobby()
    {
        return createLobby( true, true );
    }

    public static String createLobby( boolean isPublic, boolean autoManaged)
    {
        String id = UUID.randomUUID().toString();
        lobbyMap.put( id, new Lobby( id, isPublic, autoManaged ) );
        return id;
    }

    public static void removeLobby( String lobbyId )
    {
        lobbyMap.remove( lobbyId );
        System.out.printf("Lobby [%s] removed\n", lobbyId);
    }

    public static void addPlayerToLobby( String playerUid, String lobbyId )
    {
        if(playerToLobbyMap.containsKey( playerUid ))
            throw new ClientVisibleException( "Cannot join another lobby when still in a lobby" );
        lobbyMap.get( lobbyId ).addPlayer( playerUid );
        playerToLobbyMap.put( playerUid, lobbyId );
    }

    public static String addPlayer( String uid )
    {
        String lobbyId = null;
        for( String key : lobbyMap.keySet() )
        {
            if(lobbyMap.get( key ).isPublic() && lobbyMap.get( key ).isAutoManaged());
            {
                lobbyId = key;
                break;
            }
        }
        if( lobbyId == null )
            lobbyId = createLobby();

        addPlayerToLobby( uid, lobbyId );
        return lobbyId;
    }

    public static void createNewGame( String lobbyId)
    {
        lobbyMap.get( lobbyId ).createNewGame( );
    }

    public static Lobby getLobby( String id )
    {
        return lobbyMap.get( id );
    }

    public static Lobby getLobbyFromPlayer( String uid )
    {
        return lobbyMap.get( playerToLobbyMap.get( uid ) );
    }

    public static void removePlayerFromLobby( String uid )
    {
        Lobby lobby = getLobbyFromPlayer( uid );
        lobby.removePlayer( uid );
        playerToLobbyMap.remove( uid );
        if(lobby.getPlayerUids().size() == 0 )
        {
            removeLobby( lobby.getId() );
        }
    }
}
