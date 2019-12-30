package com.rptr1.fpg.msgs;

import com.rptr1.fpg.game.AbilityParameters;

public class AbilityTriggeredMsg extends Message
{
    private String uid;
    private String abilityName;
    private AbilityParameters params;

    public AbilityTriggeredMsg() {}

    public AbilityTriggeredMsg( String abilityName, AbilityParameters params )
    {
        super( "abilityTriggeredMsg" );
        this.abilityName = abilityName;
        this.params = params;
    }

    public String getAbilityName()
    {
        return abilityName;
    }

    public AbilityParameters getParams()
    {
        return params;
    }

    public void setUid( String uid )
    {
        this.uid = uid;
    }
}
