package com.rptr1.fpg.msgs;

import com.rptr1.fpg.game.Ability;
import com.rptr1.fpg.game.Player;
import com.rptr1.fpg.util.Vector2f;

public class CreatePlayerResponse extends Message
{
    private String displayName;
    private String uid;
    private Vector2f position;
    private float moveSpeed;
    private Ability[] abilities;

    public CreatePlayerResponse( Player player )
    {
        super("createPlayerResponse" );
        this.displayName = "Unknown";
        this.uid = player.getUid();
        this.position = player.getPosition();
        this.moveSpeed = player.getMoveSpeed();
        this.abilities = player.getAbilities();
    }
}
