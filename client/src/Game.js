class Game
{

    static scene;
    static camera;
    static renderer;
    static raycastPlane;
    static gameBoard;
    static directionalLight;
    static ambientLight;
    static players = {};
    static nonPlayerEntities = [];
    static zoomFactor;
    static gameStartTime = 0;
    static gameTimeOffset = 0;
    static gameStopTime = 0;
    static running = false;
    static realTimeSyncOffset = 0;

    static init( width, height )
    {
        Game.camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );
        Game.zoomFactor = width / 8;
        Game.camera.position.z = 5 * Game.zoomFactor;
        Game.camera.position.y = -3 * Game.zoomFactor;
        Game.camera.rotation.x = 0.4;

        Game.renderer = new THREE.WebGLRenderer( { antialias: true } );
        Game.renderer.setSize( window.innerWidth, window.innerHeight );
        document.body.appendChild( Game.renderer.domElement );

        Game.raycastPlane = new THREE.Mesh( new THREE.PlaneGeometry( width, height, 1, 1 ), new THREE.MeshBasicMaterial( { visible: false } ) );
        Game.gameBoard = new GameBoard( width, height );
        Game.directionalLight = new THREE.DirectionalLight( 0xffffff, 0.6 );
        Game.directionalLight.position.z = 2 * Game.zoomFactor;
        Game.ambientLight = new THREE.AmbientLight( 0x404040, 0.6 );

        Game.scene = new THREE.Scene();
        Game.scene.add( Game.raycastPlane );
        Game.scene.add( Game.gameBoard );
        Game.scene.add( Game.directionalLight );
        Game.scene.add( Game.ambientLight );
    }

    static addPlayer( player )
    {
        Game.players[ player.uid ] = player;
        Game.scene.add( player );
    }

    static addEntity( entity )
    {
        Game.nonPlayerEntities.push(entity);
        Game.scene.add(entity);
    }

    static start( startTime )
    {
        if( Game.gameStartTime !== 0 )
        {
            console.error( "Cannot start game that has already been started" );
            return;
        }
        if( startTime === undefined)
            startTime = Game.getRealTime();
        Game.gameStartTime = startTime;
        Game.running = true;
        Game.updateGraphics();
    }

    static stop( stopTime )
    {
        if( stopTime === undefined )
            stopTime = Game.getRealTime();
        Game.gameStopTime = stopTime;
        Game.running = false;
    }

    static resume( resumeTime )
    {
        if( Game.running )
        {
            console.warn( "Cannot resume game that is not stopped" );
            return;
        }
        if( resumeTime === undefined )
            resumeTime = Game.getRealTime();
        Game.gameTimeOffset += resumeTime - Game.gameStopTime;
        Game.running = true;
        Game.updateGraphics();
    }

    static updateGraphics()
    {
        // Game.gameBoard.rotation.z += 0.01;
        Game.nonPlayerEntities.forEach( function( entity, index )
        {
           if(!entity.update())
           {
               Game.scene.remove( entity );
               Game.nonPlayerEntities.splice(index, 1 );
           }
        });
        Object.keys( Game.players ).forEach( function( uid )
        {
            Game.players[ uid ].updateGraphics();
        } );
        Game.gameBoard.updateGraphics();

        Game.renderer.render( Game.scene, Game.camera );
        if( Game.running )
            requestAnimationFrame( Game.updateGraphics );
    }

    static getTime()
    {
        if( !Game.running )
            return this.gameStopTime - ( this.gameStartTime + this.gameTimeOffset );
        return Game.getRealTime() - ( this.gameStartTime + this.gameTimeOffset );
    }

    static getRealTime()
    {
        return Date.now() + this.realTimeSyncOffset;
    }

    static convertGameToTileCoords( coords )
    {
        let x = Math.floor( coords.x + Game.gameBoard.width / 2);
        let y = Math.floor( coords.y + Game.gameBoard.height / 2);
        return {x:x, y:y}
    }
}