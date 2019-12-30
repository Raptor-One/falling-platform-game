package com.rptr1.fpg.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rptr1.fpg.game.Ability;
import com.rptr1.fpg.game.Tile;

public class CustomGson
{
    private static Gson gson;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter( Tile.State.class, new Tile.StateSerializer());
        gsonBuilder.registerTypeAdapter( Ability.class, new Ability.AbilitySerializer());
        gson = gsonBuilder.create();
    }
    public static Gson getGson()
    {
        return gson;
    }
}
