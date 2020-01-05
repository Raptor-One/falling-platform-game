package com.rptr1.fpg.game;

import java.util.ArrayList;
import java.util.List;

public class Lobby
{
    private String id;
    private List<String> playerUids = new ArrayList<>();
    private Game game = null;

    public Lobby( String id )
    {
        this.id = id;
    }

    void createNewGame( )
    {
        game = new Game( id, playerUids, this::gameEnded );
    }

    void addPlayer( String playerUid )
    {
        playerUids.add( playerUid );
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
