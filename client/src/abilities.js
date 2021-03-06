const removeTileAbility = "removeTileAbility";
const throwGrenadeAbility = "throwGrenadeAbility";
const removeTileAbilityCooldown = 500; //ms
const throwGrenadeAbilityCooldown = 30; //ms
const throwWindCooldown = 30; //ms
const throwGernadeAbilityRange = 5;
const abilities = {
    removeTileAbility: {
        name: "removeTileAbility",
        condition: function( params )
        {
            let pos = Game.convertGameToTileCoords( params );
            let timeDelta = params.usedTime - 0;
            let usable = timeDelta > removeTileAbilityCooldown && Game.gameBoard.platform[ pos.x ][ pos.y ].state === 1;
            return { usable: usable, cooldownProgress: timeDelta / removeTileAbilityCooldown }
        },
        action: function( params )
        {
            let pos = Game.convertGameToTileCoords( params );
            let actionTime = params.usedTime;
            params.removeTileLastUsed = actionTime;
            Game.gameBoard.setBoardValue( pos, { state: 2, changedTime: actionTime } );
        },
        clientAutoRun: true
    },
    throwGrenadeAbility: {
        name: "throwGrenadeAbility",
        condition: function( params )
        {
            let timeDelta = params.usedTime - 0;
            let usable = timeDelta > throwGrenadeAbilityCooldown; // todo check range as well
            return { usable: usable, cooldownProgress: timeDelta / throwGrenadeAbilityCooldown }
        },
        action: function( params )
        {
            let actionTime = params.usedTime;
            params.thrwoGernadeLastUsed = actionTime;
            Game.addEntity( new Grenade( params.origin.x, params.origin.y, params.target.x, params.target.y, actionTime));
        },
        clientAutoRun: false
    },
    throwWindAbility: {
        name: "throwWindAbility",
        condition: function( params )
        {
            let timeDelta = params.usedTime - 0;
            let usable = timeDelta > throwWindCooldown; // todo check range as well
            return { usable: usable, cooldownProgress: timeDelta / throwGrenadeAbilityCooldown }
        },
        action: function( params )
        {
            let actionTime = params.usedTime + Math.min(Game.getTime() - params.usedTime, 100);
            params.throwWindLastUsed = actionTime;
            let playerPos = Game.players[params.playerUid].position;
            Game.addEntity( new Wind( playerPos.x, playerPos.y, params.target.x, params.target.y, actionTime, params.playerUid));
        },
        clientAutoRun: false
    }
};
