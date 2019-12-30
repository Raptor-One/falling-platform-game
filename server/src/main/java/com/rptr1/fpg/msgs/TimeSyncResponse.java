package com.rptr1.fpg.msgs;

public class TimeSyncResponse extends Message
{
    private long clientTimestamp;
    private long serverTimestamp;

    public TimeSyncResponse( long clientTimestamp, long serverTimestamp )
    {
        super( "timeSyncResponse" );
        this.clientTimestamp = clientTimestamp;
        this.serverTimestamp = serverTimestamp;
    }

    public long getClientTimestamp()
    {
        return clientTimestamp;
    }

    public long getServerTimestamp()
    {
        return serverTimestamp;
    }

}
