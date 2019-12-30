package com.rptr1.fpg.msgs;

public class CreateGameResponse extends Message
{
    private int width;
    private int height;
    private long startTime;

    public CreateGameResponse( int width, int height, long startTime )
    {
        super( "createGameResponse" );
        this.width = width;
        this.height = height;
        this.startTime = startTime;
    }
}
