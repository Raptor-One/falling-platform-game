package com.rptr1.fpg.game;

import com.rptr1.fpg.msgs.*;
import com.rptr1.fpg.util.Vector2f;
import com.rptr1.fpg.util.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Game
{
    public static final float MOVE_SPEED_FACTOR = 0.001f;
    private String lobbyId;
    private Tile[][] gameboard;
    private int width;
    private int height;
    private long startTime = 0;
    private long stopTime = 0;
    private long offsetTime = 0;
    private boolean running = false;
    private Map<String, Player> playerMap = new HashMap<>();
    private BlockingQueue<GameEvent> gameEventQueue;

    public Game( String lobbyId, List<String> playerUids, BlockingQueue<GameEvent> gameEventQueue )
    {
        this.lobbyId = lobbyId;
        int dimensions = calculateDimensions( playerUids.size() );
        this.width = dimensions;
        this.height = dimensions;
        this.gameboard = new Tile[ width ][ height ];
        this.gameEventQueue = gameEventQueue;
        GameEvent addPlayersGameEvent = new GameEvent();
        double spawnAngle = 0;
        float spawnRadius = dimensions / 2f * 0.8f;
        for( String uid : playerUids )
        {
            double x = spawnRadius * Math.cos(spawnAngle);
            double y = spawnRadius * Math.sin(spawnAngle);
            Player player = new Player( uid, new Vector2f( (float)x, (float)y ) );

            addPlayersGameEvent.addRecipient( uid );
            addPlayersGameEvent.addMessage( new CreatePlayerResponse( player ) );
            playerMap.put( uid, player );

            spawnAngle += (2*Math.PI) / playerUids.size();
        }
        for( int x = 0; x < this.width; x++ )
        {
            for( int y = 0; y < this.height; y++ )
            {
                this.gameboard[ x ][ y ] = new Tile( Tile.State.NORMAL, 0 );
            }
        }
        startTime = System.currentTimeMillis() + 3000;
        gameEventQueue.add( new GameEvent( playerUids, new CreateGameResponse( this.width, this.height, this.startTime ) ) );
        gameEventQueue.add( addPlayersGameEvent );
    }

    /**
     * @return list of positions of tile states that have been changed. Value A in box is sender update list, value B is other player update list
     */
    public void updateBoard( Vector2i pos, Tile tile, String playerUid )
    {
        if( !tile.getState().canOverride( this.gameboard[ pos.getX() ][ pos.getY() ].getState().toInt() ) )
        {
            BoardStateChangeResponse stateChangeResponse = new BoardStateChangeResponse( pos, this.gameboard[ pos.getX() ][ pos.getY() ] );
            gameEventQueue.add( new GameEvent( playerUid, stateChangeResponse ) );
            return;
        }
        List<Message> messages = new ArrayList<>();
        this.gameboard[ pos.getX() ][ pos.getY() ] = tile;
        messages.add( new BoardStateChangeResponse( pos, this.gameboard[ pos.getX() ][ pos.getY() ] ) );
        //todo check for platforms that should fall
        gameEventQueue.add( new GameEvent( new ArrayList<>( playerMap.keySet() ), messages ) );
    }

    public Vector2i convertGameToTilePosition( Vector2f coords )
    {
        int x = (int) ( coords.getX() + this.width / 2 );
        int y = (int) ( coords.getY() + this.height / 2 );
        return new Vector2i( x, y );
    }

    public boolean isValidTilePosition( Vector2i coords )
    {
        return 0 <= coords.getX() && coords.getX() < this.width && 0 <= coords.getY() && coords.getY() < this.height;
    }

    public Player getPlayer( String uid )
    {
        return playerMap.get( uid );
    }

    public long getTime()
    {
        if( !this.running )
            return this.stopTime - ( this.startTime + this.offsetTime );
        return System.currentTimeMillis() - ( this.startTime + this.offsetTime );
    }

    private int calculateDimensions( int numOfPlayers )
    {
        return 10 + numOfPlayers * 2;
    }
}