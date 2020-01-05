package com.rptr1.fpg.game;

import com.rptr1.fpg.util.Vector2f;

public class Player
{
    private String uid;
    private Vector2f position;
    private float moveSpeed = 5.f;
    private Ability[] abilities = { Ability.THROW_WIND };

    public Player( String uid, Vector2f position )
    {
        this.uid = uid;
        this.position = position;
    }

    public String getUid()
    {
        return uid;
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public float getMoveSpeed()
    {
        return moveSpeed;
    }

    public Ability[] getAbilities()
    {
        return abilities;
    }
}
