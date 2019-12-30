package com.rptr1.fpg.util;

public class Vector2i
{
    private int x;
    private int y;

    public Vector2i(){}

    public Vector2i( int x, int y )
    {
        this.x = x;
        this.y = y;
    }

    public Vector2i subtract(Vector2i other)
    {
        return new Vector2i( this.getX() - other.getX(), this.getY() - other.getY() );
    }

    public int getX()
    {
        return x;
    }

    public void setX( int x )
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY( int y )
    {
        this.y = y;
    }
}
