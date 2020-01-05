package com.rptr1.fpg.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rptr1.fpg.game.Ability;
import com.rptr1.fpg.game.Effect;
import com.rptr1.fpg.game.Tile;

public class CustomGson
{
    private static Gson gson;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter( Tile.State.class, new Tile.StateSerializer());
        gsonBuilder.registerTypeAdapter( Ability.class, new Ability.AbilitySerializer());
        gsonBuilder.registerTypeAdapter( Effect.class, new Effect.EffectSerializer());
        gson = gsonBuilder.create();
    }
    public static Gson getGson()
    {
        return gson;
    }
}
