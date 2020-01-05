package com.rptr1.fpg.msgs;

import com.rptr1.fpg.game.Lobby;
import com.rptr1.fpg.game.LobbyManager;
import com.rptr1.fpg.server.main.GameEventDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class UpdatePlayerMovementHandler extends MessageHandler<UpdatePlayerMovementMsg>
{
    @Override
    public void handle( UpdatePlayerMovementMsg msg, String playerUid )
    {
        List<String> original = LobbyManager.getLobbyFromPlayer( playerUid ).getPlayerUids();
        List<String> clone = new ArrayList<>( original );
        clone.remove( playerUid );
        msg.setUid( playerUid );
        GameEventDispatcher.dispatch( new GameEvent( clone, msg) );
    }
}
