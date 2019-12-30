package com.rptr1.fpg.util;

public class Vector2f
{
    private float x;
    private float y;

    public Vector2f(){}

    public Vector2f( float x, float y )
    {
        this.x = x;
        this.y = y;
    }

    public Vector2f subtract(Vector2f other)
    {
        return new Vector2f( this.getX() - other.getX(), this.getY() - other.getY() );
    }

    public float getX()
    {
        return x;
    }

    public void setX( float x )
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    public void setY( float y )
    {
        this.y = y;
    }
}
