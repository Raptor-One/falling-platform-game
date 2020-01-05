package com.rptr1.fpg.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public enum Effect
{
    PUSH("pushEffect", (params, game, player) -> {} )

    ;

    private String name;
    private EffectAction effectFunction;
    private static final Map<String, Effect> nameToEffectMap = new HashMap<>();
    static {
        for(Effect effect : Effect.values()) {
            nameToEffectMap.put(effect.name, effect);
        }
    }

    Effect( String name, EffectAction effectFunction )
    {
        this.name = name;
        this.effectFunction = effectFunction;
    }

    public static Effect getFromName( String name )
    {
        return nameToEffectMap.get( name );
    }

    public void performAction( EffectParameters effectParameters, Game game, Player player )
    {
        this.effectFunction.act( effectParameters, game, player );
    }

    interface EffectAction
    {
        void act( EffectParameters effectParameters, Game game, Player player );
    }

    public static class EffectSerializer implements JsonSerializer<Effect>
    {

        @Override
        public JsonElement serialize( Effect src, Type typeOfSrc, JsonSerializationContext context )
        {
            return new JsonPrimitive( src.name );
        }
    }
}
