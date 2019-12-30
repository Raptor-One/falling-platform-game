package com.rptr1.fpg.game;

import com.rptr1.fpg.util.Vector2f;

public class AbilityParameters
{
    Vector2f origin;
    Vector2f target;
    long usedTime;

    public Vector2f getTarget()
    {
        return target;
    }

    public void setTarget( Vector2f target )
    {
        this.target = target;
    }

    public long getUsedTime()
    {
        return usedTime;
    }

    public void setUsedTime( long usedTime )
    {
        this.usedTime = usedTime;
    }

    public Vector2f getOrigin()
    {
        return origin;
    }

    public void setOrigin( Vector2f origin )
    {
        this.origin = origin;
    }
}
