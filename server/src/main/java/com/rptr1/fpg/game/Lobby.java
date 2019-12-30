package com.rptr1.fpg.game;

import com.rptr1.fpg.msgs.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Lobby
{
    private String id;
    private List<String> playerUids = new ArrayList<>();
    private Game game = null;

    public Lobby( String id )
    {
        this.id = id;
    }

    void createNewGame( BlockingQueue<GameEvent> gameEventQueue )
    {
        game = new Game( id, playerUids, gameEventQueue );
    }

    void addPlayer( String playerUid )
    {
        playerUids.add( playerUid );
    }

    void removePlayer( String playerUid )
    {
        playerUids.remove( playerUid );
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
}
