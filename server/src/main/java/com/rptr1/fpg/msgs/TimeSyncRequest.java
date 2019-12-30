package com.rptr1.fpg.msgs;

public class TimeSyncRequest extends Message
{
    private long timestamp;

    public TimeSyncRequest( long timestamp )
    {
        super("timeSyncRequest");
        this.timestamp = timestamp;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}
