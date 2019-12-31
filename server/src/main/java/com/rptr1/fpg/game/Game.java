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
    private Runnable onGameOver;
    private BlockingQueue<GameEvent> gameEventQueue;

    public Game( String lobbyId, List<String> playerUids, Runnable onGameOver, BlockingQueue<GameEvent> gameEventQueue )
    {
        this.lobbyId = lobbyId;
        int dimensions = calculateDimensions( playerUids.size() );
        this.width = dimensions;
        this.height = dimensions;
        this.gameboard = new Tile[ width ][ height ];
        this.onGameOver = onGameOver;
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

    public void updateBoard( Vector2i pos, Tile tile )
    {
        if( !tile.getState().canOverride( this.gameboard[ pos.getX() ][ pos.getY() ].getState().toInt() ) )
        {
            return;
        }
        this.gameboard[ pos.getX() ][ pos.getY() ] = tile;
        //todo check for platforms that should fall
    }

    public void removeDisconnectedPlayer( String uid )
    {
        List<String> remainingPlayers = new ArrayList<>( playerMap.keySet() );
        remainingPlayers.remove( uid );
        gameEventQueue.add( new GameEvent( remainingPlayers , new RemovePlayerResponse( uid ) ) );
        playerMap.remove( uid );
        if(playerMap.size() == 0)
        {
            // todo end / restart game logic
            onGameOver.run();
        }
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
