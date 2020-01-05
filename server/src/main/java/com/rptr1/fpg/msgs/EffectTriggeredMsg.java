package com.rptr1.fpg.msgs;

import com.rptr1.fpg.game.EffectParameters;

public class EffectTriggeredMsg extends Message
{
    private String uid;
    private String targetUid;
    private String effectName;
    private EffectParameters params;

    public EffectTriggeredMsg() {}

    public EffectTriggeredMsg( String abilityName, EffectParameters params )
    {
        super( "effectTriggeredMsg" );
        this.effectName = abilityName;
        this.params = params;
    }

    public String getEffectName()
    {
        return effectName;
    }

    public EffectParameters getParams()
    {
        return params;
    }

    public void setUid( String uid )
    {
        this.uid = uid;
    }
}
