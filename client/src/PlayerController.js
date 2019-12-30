class PlayerController
{
    static mouse = new THREE.Vector2();
    static raycaster = new THREE.Raycaster();

    static mouseUpdateListener( e )
    {
        PlayerController.mouse = PlayerController.convertScreenToScale( e );
    }

    static convertScreenToScale( e )
    {
        let scale = new THREE.Vector2();
        scale.x = ( e.clientX / window.innerWidth ) * 2 - 1;
        scale.y = -( e.clientY / window.innerHeight ) * 2 + 1;
        return scale;
    }

    static convertToGameCoords( e )
    {
        if( e.clientX === undefined || e.clientY === undefined )
            return undefined;
        PlayerController.raycaster.setFromCamera( PlayerController.convertScreenToScale( e ), Game.camera );
        let intersects = PlayerController.raycaster.intersectObjects( [ Game.raycastPlane ] );
        if( intersects.length === 1 )
            return intersects[ 0 ].point;
    }

    controlFunctions = {
        setPlayerTarget: this.setPlayerTarget,
        usePrimaryAbility: this.usePrimaryAbility
    };

    constructor( player, gameManager, controls = {} )
    {
        this.player = player;
        this.gameManager = gameManager;
        this.controls = controls;
        this.setControls( controls );

        PlayerController.raycaster = new THREE.Raycaster();
        PlayerController.mouse = new THREE.Vector2();

        let nthis = this;
        window.addEventListener( 'click', function( e )
        {
            nthis.mouseClickListener( e, nthis );
        }, false );
        window.addEventListener( 'contextmenu', function( e )
        {
            e.preventDefault();
            nthis.mouseClickListener( e, nthis );
        }, false );

        window.addEventListener( 'keyup', this.keyUpListener, false );
        window.addEventListener( 'keydown', this.keyDownListener, false );
    }

    setControls( controls )
    {
        let nthis = this;
        Object.keys( controls ).forEach( function( key )
        {
            nthis.controls[ key ] = nthis.controlFunctions[ controls[ key ] ];
        } );
    }

    setPlayerTarget( e, nthis = this )
    {
        let point = PlayerController.convertToGameCoords( e );
        if( point === undefined ) return;

        let lastPos = {};
        let lastPosTime = Game.getTime();
        lastPos.x = nthis.player.position.x;
        lastPos.y = nthis.player.position.y;

        nthis.gameManager.setNewPlayerTarget(lastPos, lastPosTime, point, nthis.gameManager);

        nthis.player.lastX = lastPos.x;
        nthis.player.lastY = lastPos.y;
        nthis.player.targetX = point.x;
        nthis.player.targetY = point.y;
        nthis.player.lastPosTime = lastPosTime;
    }

    usePrimaryAbility( e, nthis = this )
    {
        let addonParams = {};
        let point = PlayerController.convertToGameCoords( e );
        if( point !== undefined )
        {
            addonParams.target = {};
            addonParams.target.x = point.x;
            addonParams.target.y = point.y;
        }
        nthis.tryUseAbility( nthis.player.getAbilityData( 0, addonParams ), nthis );
    }

    tryUseAbility( abilityData, nthis = this )
    {
        if( abilityData.ability.condition( abilityData.params ).usable )
        {
            nthis.gameManager.triggerAbility( abilityData.ability.name, abilityData.params, nthis.gameManager );
            if( abilityData.ability.clientAutoRun )
                abilityData.ability.action( abilityData.params );
        }
    }

    keyUpListener( e )
    {
        console.log( e );
    }

    keyDownListener( e )
    {
        console.log( e );
    }

    mouseClickListener( e, nthis = this )
    {
        let control;
        switch( e.which )
        {
            case 1:
                control = 'leftClick';
                break;
            case 3:
                control = 'rightClick';
                break;
        }
        if( nthis.controls.hasOwnProperty( control ) )
            nthis.controls[ control ]( e, nthis );
        console.log( e );
    }

}

window.addEventListener( 'mousemove', PlayerController.mouseUpdateListener, false );
window.addEventListener( 'contextmenu', function( e )
{
    e.preventDefault()

}, false );
