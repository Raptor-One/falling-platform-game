package com.rptr1.fpg.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.function.Function;

public class Tile
{
    private State state;
    private long changedTime;

    public Tile(){}

    public Tile( State state, long changedTime )
    {
        this.state = state;
        this.changedTime = changedTime;
    }

    public State getState()
    {
        return state;
    }

    public void setState( State state )
    {
        this.state = state;
    }

    public long getChangedTime()
    {
        return changedTime;
    }

    public void setChangedTime( long changedTime )
    {
        this.changedTime = changedTime;
    }

    public enum State {
        GONE(0, (state) -> false ),
        NORMAL(1, (state) -> true ),
        DISINTEGRATING(2, (state) -> state == NORMAL.toInt() ),
        FALLING(3, (state) -> state == NORMAL.toInt() ),

        ;

        private int value;
        private Function<Integer,Boolean> canOverride;

        State(int value, Function<Integer,Boolean> canOverride)
        {
            this.value = value;
            this.canOverride = canOverride;
        }

        public int getValue()
        {
            return value;
        }

        public int toInt()
        {
            return value;
        }


        public boolean canOverride( int stateToOverride )
        {
            return canOverride.apply( stateToOverride );
        }
    }

    public static class StateSerializer implements JsonSerializer<State>
    {
        @Override
        public JsonElement serialize( State src, Type typeOfSrc, JsonSerializationContext context )
        {
            return new JsonPrimitive( src.toInt() );
        }
    }
}
