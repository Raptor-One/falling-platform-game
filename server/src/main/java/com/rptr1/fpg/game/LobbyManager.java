package com.rptr1.fpg.game;

import java.util.HashMap;
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
    }

    public static void addPlayerToLobby( String playerUid, String lobbyId )
    {
        lobbyMap.get( lobbyId ).addPlayer( playerUid );
        playerToLobbyMap.put( playerUid, lobbyId );
    }

    public static String addPlayer( String uid )
    {
        String lobbyId;
        if( lobbyMap.size() == 0 )
            lobbyId = createLobby();
        else
            lobbyId = lobbyMap.keySet().iterator().next();
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
