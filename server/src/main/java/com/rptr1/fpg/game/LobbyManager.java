package com.rptr1.fpg.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbyManager
{
    private static Map<String, Lobby> lobbyMap = new HashMap<>();
    private static Map<String, String> playerToLobbyMap = new HashMap<>();

    public static String createLobby()
    {
        String id = UUID.randomUUID().toString();
        lobbyMap.put( id, new Lobby( id ) );
        return id;
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
        getLobbyFromPlayer( uid ).removePlayer( uid );
    }
}
