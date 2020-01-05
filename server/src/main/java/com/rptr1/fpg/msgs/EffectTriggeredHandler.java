package com.rptr1.fpg.msgs;

import com.rptr1.fpg.error.ClientVisibleException;
import com.rptr1.fpg.game.*;
import com.rptr1.fpg.server.main.GameEventDispatcher;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class EffectTriggeredHandler extends MessageHandler<EffectTriggeredMsg>
{
    @Override
    public void handle( EffectTriggeredMsg msg, String playerUid )
    {
        Lobby lobby = LobbyManager.getLobbyFromPlayer( playerUid );
        if( lobby == null )
            throw new ClientVisibleException( "Player is not in a lobby" );
        Game game = lobby.getGame();
        if( game == null )
            throw new ClientVisibleException( "Player's lobby is not in a game" );
        Player player = game.getPlayer( playerUid );
        if( player == null )
            throw new ClientVisibleException( "Player is not in this lobby's game" );
        Effect effect = Effect.getFromName( msg.getEffectName() );
        if( effect == null )
            throw new ClientVisibleException( "Unknown Effect" );

        List<String> players = LobbyManager.getLobbyFromPlayer( playerUid ).getPlayerUids();
        msg.setUid( playerUid );
        GameEventDispatcher.dispatch( new GameEvent( players, msg) );

//        effect.performAction( msg.getParams(), game, player );
    }
}
