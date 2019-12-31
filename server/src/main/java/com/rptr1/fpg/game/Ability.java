package com.rptr1.fpg.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.rptr1.fpg.util.Vector2f;
import com.rptr1.fpg.util.Vector2i;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public enum Ability
{
    REMOVE_TILE( "removeTileAbility", 300,
            ( params, game, player ) -> {
                Vector2i tileCoords = game.convertGameToTilePosition( params.getTarget() );
                if( !game.isValidTilePosition( tileCoords ) ) return false;
//                if( game.getTime() - params.getUsedTime() < 300) return false;
                return true;
            },
            ( params, game, player ) -> {
                Vector2i tileCoords = game.convertGameToTilePosition( params.getTarget() );
                game.updateBoard( tileCoords, new Tile( Tile.State.DISINTEGRATING, params.usedTime) );
            }),
    THROW_GRENADE("throwGrenadeAbility", 500,
            ( params, game, player ) -> {
                Vector2i tileCoords = game.convertGameToTilePosition( params.getTarget() );
                if( !game.isValidTilePosition( tileCoords ) ) return false;
//                if( game.getTime() - params.getUsedTime() < 500) return false;
                return true;
            },
            ( params, game, player ) -> {
                Vector2i tileCoords = game.convertGameToTilePosition( params.getTarget() );
                Vector2f delta = params.getTarget().subtract( params.getOrigin() );
                double distance = Math.sqrt( Math.pow( delta.getX(), 2 ) + Math.pow( delta.getY(), 2 ) );
                long timeTillResult = (long)( distance / (0.01) + 100);
                game.updateBoard( tileCoords, new Tile( Tile.State.DISINTEGRATING, params.usedTime + timeTillResult) );
            }),

    ;

    private String name;
    private int cooldown;
    private AbilityCondition conditionFunction;
    private AbilityAction actionFunction;
    private static final Map<String, Ability> nameToAbilityMap = new HashMap<>();
    static {
        for(Ability ability : Ability.values()) {
            nameToAbilityMap.put(ability.name, ability);
        }
    }

    Ability( String name, int cooldown, AbilityCondition conditionFunction, AbilityAction actionFunction )
    {
        this.name = name;
        this.cooldown = cooldown;
        this.conditionFunction = conditionFunction;
        this.actionFunction = actionFunction;
    }

    public static Ability getFromName( String name )
    {
        return nameToAbilityMap.get( name );
    }
    public int getCooldown()
    {
        return cooldown;
    }

    public boolean verifyCondition( AbilityParameters abilityParameters, Game game, Player player )
    {
        return this.conditionFunction.act( abilityParameters, game, player );
    }
    public void performAction( AbilityParameters abilityParameters, Game game, Player player )
    {
        this.actionFunction.act( abilityParameters, game, player );
    }

    interface AbilityCondition
    {
        boolean act( AbilityParameters abilityParameters, Game game, Player player );
    }

    interface AbilityAction
    {
        void act( AbilityParameters abilityParameters, Game game, Player player );
    }


    public static class AbilitySerializer implements JsonSerializer<Ability>
    {

        @Override
        public JsonElement serialize( Ability src, Type typeOfSrc, JsonSerializationContext context )
        {
            return new JsonPrimitive( src.name );
        }
    }

}
