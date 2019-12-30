const url = "ws://localhost";
const port = "8887";
const defaultControls = { leftClick: 'usePrimaryAbility', rightClick: 'setPlayerTarget' };

class GameManager
{
    messageHandlers = {
        timeSyncResponse: this.setTimeHandler,
        boardStateChangeResponse: this.boardStateChangeHandler,
        createPlayerResponse: this.createPlayerHandler,
        removePlayerResponse: this.removePlayerHandler,
        createGameResponse: this.createGameHandler,
        setUidResponse: this.setUidMsgHandler,
        updatePlayerMovementMsg: this.playerPosChangeHandler,
        abilityTriggeredMsg: this.playerUsedAbilityHandler,
    };

    realTimeSyncOffset = 0;

    constructor()
    {
        this.ws = new WebSocket( url + ":" + port );
        let nthis = this;
        this.ws.onopen = function()
        {
            nthis.onOpen( nthis );
        };

        this.ws.onmessage = function( evt )
        {
            nthis.onMessage( evt, nthis );
        };

        this.ws.onclose = function()
        {
            nthis.onClose( nthis );
        }
    }


    onOpen( nthis = this )
    {
        console.log( "Connected to server socket." );
    }

    onMessage( evt, nthis = this )
    {
        let msg = JSON.parse( evt.data );
        console.log( "Message received: " );
        console.log( msg );
        nthis.handleMsg( msg, nthis )
    }

    onClose( nthis = this )
    {
        console.log( "Connected to server has closed." )
    }

    handleMsg( msg, nthis = this )
    {
        if( !nthis.messageHandlers.hasOwnProperty( msg.msgType ) )
        {
            console.warn( "Received message with unknown type: " + msg.msgType );
            return;
        }
        nthis.messageHandlers[ msg.msgType ]( msg, nthis );
    }

    sendMsg( msg, nthis = this )
    {
        if(nthis.ws.readyState === nthis.ws.CONNECTING)
        {
            console.warn("Unable to send data, still connecting to server.");
            return;
        }
        if(nthis.ws.readyState >= nthis.ws.CLOSING)
        {
            console.error("Unable to send data, server disconnected.");
            return;
        }
        nthis.ws.send( JSON.stringify( msg ) );
    }

    requestTimeSync( msg, nthis = this )
    {
        nthis.sendMsg( { msgType: "timeSyncRequest", timestamp: Date.now() } );
    }

    triggerAbility( abilityName, params, nthis = this )
    {
        nthis.sendMsg( { msgType: "abilityTriggeredMsg", abilityName: abilityName, params: params } );
    }

    setNewPlayerTarget( lastPos, lastPosTime, targetPos, nthis = this )
    {
        nthis.sendMsg( { msgType: "updatePlayerMovementMsg", lastPosition: lastPos, lastPositionTime: lastPosTime, targetPosition: targetPos } );
    }

    setTimeHandler( msg, nthis = this )
    {
        let currentTime = Date.now();
        let ping = ( currentTime - msg.clientTimestamp ) / 2;
        nthis.realTimeSyncOffset = Math.round( ( msg.serverTimestamp + ping ) - currentTime );
        Game.realTimeSyncOffset = nthis.realTimeSyncOffset;
        console.log( `Time synced: ping = ${ping}, timeOffset = ${nthis.realTimeSyncOffset}` );
    }

    setUidMsgHandler( msg, nthis = this )
    {
        nthis.uid = msg.uid;
        console.log( "Received and set uid: " + msg.uid)
        console.log( "Requesting time sync." );
        nthis.requestTimeSync();
    }

    createGameHandler( msg, nthis = this )
    {
        //todo cleanup old game if exists
        Game.init( msg.width, msg.height );

        // let player = new Player("Joe", 0, 0, 5, [throwGrenadeAbility]);
        // let controls = new PlayerController(player, gameManager, {leftClick: 'usePrimaryAbility', rightClick: 'setPlayerTarget' });
        // Game.addPlayer( player );

        Game.start( msg.startTime );
    }

    createPlayerHandler( msg, nthis = this )
    {
        let player = new Player( msg.uid, msg.position.x, msg.position.y, msg.moveSpeed, msg.abilities );
        Game.addPlayer( player );
        if( nthis.uid !== undefined && nthis.uid === msg.uid )
        {
            if( nthis.controller !== undefined )
                delete nthis.controller;
            nthis.controller = new PlayerController( player, nthis, defaultControls );
        }
    }

    removePlayerHandler( msg, nthis = this )
    {
        Game.removePlayer( msg.uid );
        if( nthis.uid !== undefined && nthis.uid === msg.uid )
        {
            if( nthis.controller !== undefined )
                delete nthis.controller;
        }
    }

    boardStateChangeHandler( msg, nthis = this )
    {
        nthis.validateGameExistence();
        Game.gameBoard.setBoardValue( msg.position, msg.tile );
    }

    playerPosChangeHandler( msg, nthis = this )
    {
        nthis.validateGameExistence();
        let player = nthis.validatePlayerExistence( msg.uid, nthis );

        player.lastPosTime = Game.getTime();
        // player.lastPosTime = msg.lastPositionTime;
        player.lastX = msg.lastPosition.x;
        player.lastY = msg.lastPosition.y;
        player.targetX = msg.targetPosition.x;
        player.targetY = msg.targetPosition.y;
    }

    playerUsedAbilityHandler( msg, nthis = this )
    {
        nthis.validateGameExistence();
        let player = nthis.validatePlayerExistence( msg.uid, nthis );

        if( abilities[ msg.abilityName ] === undefined )
        {
            console.error( "Player used ability which does not exist to this client" );
            return;
        }
        abilities[ msg.abilityName ].action( msg.params );
    }

    validateGameExistence( nthis = this )
    {
        if( Game.gameStartTime === 0 )
        {
            throw "Attempted player position change with no game";
        }
    }

    validatePlayerExistence( uid, nthis = this )
    {
        let player = Game.players[ uid ];
        if( player === undefined )
        {
            throw "Received player position change for player which does not exist";
        }
        return player
    }


}