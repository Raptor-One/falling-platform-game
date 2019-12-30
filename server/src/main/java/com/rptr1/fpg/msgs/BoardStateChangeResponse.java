package com.rptr1.fpg.msgs;

import com.rptr1.fpg.game.Tile;
import com.rptr1.fpg.util.Vector2i;

public class BoardStateChangeResponse extends Message
{
    private Vector2i position;
    private Tile tile;

    public BoardStateChangeResponse( Vector2i position, Tile tile )
    {
        super("boardStateChangeResponse");
        this.position = position;
        this.tile = tile;
    }
}
