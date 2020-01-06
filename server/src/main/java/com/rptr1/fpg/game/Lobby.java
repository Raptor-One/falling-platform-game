package com.rptr1.fpg.game;

import java.util.ArrayList;
import java.util.List;

public class Lobby
{
    private static final int START_PLAYER_COUNT = 2;
    private String id;
    private boolean isPublic;
    private boolean autoManaged;
    private List<String> playerUids = new ArrayList<>();
    private Game game = null;

    public Lobby( String id, boolean isPublic, boolean autoManaged )
    {
        this.id = id;
        this.isPublic = isPublic;
        this.autoManaged = autoManaged;
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

    public String getId()
    {
        return id;
    }
}
