const defaultPlayerColor = new THREE.Color( 0.8, 0.8, 0.8 );
const moveSpeedFactor = 0.001;
const acceptableDelta = 0.001;

class Player extends Entity
{
    effects = [];

    abilityParams = {
        playerUid: this.uid,
        origin: { x: undefined, y: undefined },
        target: { x: undefined, y: undefined },
        usedTime: 0,
    };

    abilityUsedTimes = [ 0, 0, 0, 0 ];

    constructor( uid, x, y, moveSpeed, abilities )
    {
        super( x, y, 0 );
        this.uid = uid;
        this.lastX = x;
        this.lastY = y;
        this.lastPosTime = 0;
        this.targetX = x;
        this.targetY = y;
        this.moveSpeed = moveSpeed * moveSpeedFactor;
        this.abilities = abilities;

        this.hud = new THREE.Group();
        super.add( this.hud );

        this.collider = new Collider( this, circleCollider( 0.4 ) );
    }

    createEntityMesh()
    {
        let player = new THREE.Group();

        let cylinder = new THREE.Mesh(
            new THREE.CylinderGeometry( 0.3, 0.4, 1, 3, 1 ),
            new THREE.MeshPhongMaterial( { color: defaultPlayerColor, flatShading: true } )
        );
        cylinder.position.z = 0.5;
        cylinder.rotation.x = Math.PI / 2;

        player.add( cylinder );

        return player;
    }

    getAbilityData( index, addonParams )
    {
        if( index >= this.abilities.length )
        {
            console.warn( "Attempted to use ability at a empty ability slot" );
            return;
        }
        let nthis = this;
        Object.keys( addonParams ).forEach( function( key )
        {
            nthis.abilityParams[ key ] = addonParams[ key ];
        } );
        this.abilityParams.origin.x = this.position.x;
        this.abilityParams.origin.y = this.position.y;
        this.abilityParams.usedTime = Game.getTime();
        if( !abilities.hasOwnProperty( this.abilities[ index ] ) )
        {
            console.error( "Attempted to use ability which does not exist" );
            return;
        }
        let ability = abilities[ this.abilities[ index ] ];
        return { ability: ability, params: nthis.abilityParams };
    }

    updateGraphics()
    {
        if( this.effects.length > 0 )
        {
            if(this.effects[0].params.startTime <= Game.getTime())
            {
                if( this.effects[ 0 ].effect.apply( this, this.effects[0].params) === false )
                    this.effects.splice( 0, 1 );
                return;
            }
        }

        if( this.position.x === this.targetX && this.position.y === this.targetY )
            return;
        let deltaX = this.targetX - this.lastX;
        let deltaY = this.targetY - this.lastY;
        let distance = calcDistance(deltaX, deltaY);
        let progress = ( Game.getTime() - this.lastPosTime ) * this.moveSpeed;
        let progressFactor = Math.min( progress / distance, 1 );
        this.position.x = this.lastX + deltaX * progressFactor;
        this.position.y = this.lastY + deltaY * progressFactor;
    }

}