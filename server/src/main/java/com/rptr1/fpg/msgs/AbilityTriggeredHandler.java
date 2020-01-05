package com.rptr1.fpg.msgs;

import com.rptr1.fpg.error.ClientVisibleException;
import com.rptr1.fpg.game.*;
import com.rptr1.fpg.server.main.GameEventDispatcher;

import java.util.List;

public class AbilityTriggeredHandler extends MessageHandler<AbilityTriggeredMsg>
{
    @Override
    public void handle( AbilityTriggeredMsg msg, String playerUid )
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
        Ability ability = Ability.getFromName( msg.getAbilityName() );
        if( ability == null )
            throw new ClientVisibleException( "Unknown Ability" );

        if( !ability.verifyCondition( msg.getParams(), game, player ) )
            throw new ClientVisibleException( "Unable to use ability" );

        List<String> players = LobbyManager.getLobbyFromPlayer( playerUid ).getPlayerUids();
        msg.setUid( playerUid );

        GameEventDispatcher.dispatch( new GameEvent( players, msg) );

        ability.performAction( msg.getParams(), game, player );
    }
}
