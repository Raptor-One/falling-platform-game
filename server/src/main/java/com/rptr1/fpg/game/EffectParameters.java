package com.rptr1.fpg.game;

import com.rptr1.fpg.util.Vector2f;

public class EffectParameters
{
    Vector2f origin;
    Vector2f target;
    long startTime;
    long duration;

    public Vector2f getTarget()
    {
        return target;
    }

    public void setTarget( Vector2f target )
    {
        this.target = target;
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
