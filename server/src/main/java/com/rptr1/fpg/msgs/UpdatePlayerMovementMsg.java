package com.rptr1.fpg.msgs;

import com.rptr1.fpg.util.Vector2f;

public class UpdatePlayerMovementMsg extends Message
{
    private String uid;
    private Vector2f lastPosition;
    private Vector2f targetPosition;
    private long lastPositionTime;

    public UpdatePlayerMovementMsg()
    {
        super("updatePlayerMovementMsg");
    }

    public void setUid( String uid )
    {
        this.uid = uid;
    }
}
