package com.rptr1.fpg.msgs;

public class LobbyInfoResponse extends Message
{
    private String lobbyId;
    private String[] playerNames;
    private String statusMessage;

    public LobbyInfoResponse( String lobbyId, String[] playerNames, String statusMessage )
    {
        super( "lobbyInfoResponse" );
        this.lobbyId = lobbyId;
        this.playerNames = playerNames;
        this.statusMessage = statusMessage;
    }
}
