package com.rptr1.fpg.msgs;

public class LobbyJoinRequest extends Message
{
    private String lobbyId = null;
    private boolean isPublic = true;
    private boolean autoManaged = true;

    public LobbyJoinRequest(  )
    {
        super( "lobbyJoinRequest" );
    }

        public String getLobbyId()
    {
        return lobbyId;
    }

    public boolean isPublic()
    {
        return isPublic;
    }

    public boolean isAutoManaged()
    {
        return autoManaged;
    }
}
