package com.rptr1.fpg.msgs;

import com.rptr1.fpg.game.LobbyManager;

public class LobbyJoinHandler extends MessageHandler<LobbyJoinRequest>
{
    @Override
    public void handle( LobbyJoinRequest msg, String playerUid )
    {
        if(msg.getLobbyId() == null)
        {
            if(msg.isPublic())
            {
                LobbyManager.addPlayer( playerUid );
                return;
            }
            else
            {
                String lobbyId = LobbyManager.createLobby( msg.isPublic(), msg.isAutoManaged() );
                LobbyManager.addPlayerToLobby( playerUid, lobbyId );
                return;
            }
        }
        LobbyManager.addPlayerToLobby( playerUid, msg.getLobbyId() );
    }
}
