package com.rptr1.fpg.game;

import com.rptr1.fpg.msgs.GameEvent;
import com.rptr1.fpg.msgs.LobbyInfoResponse;
import com.rptr1.fpg.server.main.GameEventDispatcher;

import java.util.ArrayList;
import java.util.List;

public class Lobby
{
    private static final int START_PLAYER_COUNT = 2;
    private String id;
    private boolean isPublic;
    private boolean autoManaged;
    private String statusMessage;
    private List<String> playerUids = new ArrayList<>();
    private Game game = null;

    public Lobby( String id, boolean isPublic, boolean autoManaged )
    {
        this.id = id;
        this.isPublic = isPublic;
        this.autoManaged = autoManaged;
        this.statusMessage = autoManaged ? "Waiting for players..." : "Waiting for host...";
    }

    void createNewGame( )
    {
        game = new Game( id, playerUids, this::gameEnded );
    }

    void addPlayer( String playerUid )
    {
        playerUids.add( playerUid );
        if( game == null && autoManaged && playerUids.size() >= START_PLAYER_COUNT )
        {
            createNewGame();
        }
        System.out.printf("Player Joined: lobby [%s] has %d players\n", id, playerUids.size());
        sendUpdate();
    }

    void gameEnded()
    {
        game = null;
    }

    void removePlayer( String playerUid )
    {
        if(game != null)
        {
            game.removeDisconnectedPlayer( playerUid );
        }
        playerUids.remove( playerUid );
        System.out.printf("Player Left: lobby [%s] has %d players\n", id, playerUids.size());
        sendUpdate();
    }

    void sendUpdate()
    {
        GameEventDispatcher.dispatch( new GameEvent( playerUids, new LobbyInfoResponse( id, (String[])playerUids.toArray(), statusMessage ) ) );
    }

    public Game getGame()
    {
        return game;
    }

    public int getNumberOfPlayers()
    {
        return playerUids.size();
    }

    public List<String> getPlayerUids()
    {
        return playerUids;
    }

    public String getId()
    {
        return id;
    }

    public boolean isPublic()
    {
        return isPublic;
    }

    public boolean isAutoManaged()
    {
        return autoManaged;
    }
}
