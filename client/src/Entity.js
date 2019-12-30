class Entity extends THREE.Group
{
    constructor( x, y, createdTime, params )
    {
        super();
        this.createdTime = createdTime;
        this.mesh = this.createEntityMesh( params );
        this.position.x = x;
        this.position.y = y;
        super.add( this.mesh )
    }

    createEntityMesh( params )
    {
        throw "createEntityMesh not implemented exception";
    }

    /**
     * @return boolean: false when lifecycle has ended
     */
    update()
    {
        throw "update not implemented exception"
    }
}

class FlashExplosion extends Entity
{
    static maxOpacity = 0.6;
    static initialScale = 0.5;
    static blowUpTime = 50; //ms
    static fadeAwayTime = 100; //ms
    static color = new THREE.Color( 1, 1, 1 );

    constructor( x, y, radius, createdTime )
    {
        super( x, y, createdTime, {radius: radius} );
        this.phase = 0;
        this.mesh.scale.set( FlashExplosion.initialScale, FlashExplosion.initialScale, FlashExplosion.initialScale );
    }

    createEntityMesh( params )
    {
        let entity = new THREE.Group();
        let sphere = new THREE.Mesh(
            new THREE.SphereGeometry( params.radius, 16, 16 ),
            new THREE.MeshBasicMaterial( { color: FlashExplosion.color, transparent: true, opacity: FlashExplosion.maxOpacity, visible: false } )
        );
        entity.add( sphere );
        return entity;
    }

    update()
    {
        if( Game.getTime() < this.createdTime ) return true;
        if( this.phase === 0 )
        {
            this.mesh.children[ 0 ].material.visible = true;
            let progressFactor = Math.min( 1, ( Game.getTime() - this.createdTime ) / FlashExplosion.blowUpTime );
            let scale = FlashExplosion.initialScale + ( 1 - FlashExplosion.initialScale ) * progressFactor;
            this.mesh.scale.set( scale, scale, scale );
            if( progressFactor === 1 )
                this.phase = 1;
        }
        if( this.phase === 1 )
        {
            let progressFactor = Math.min( 1, ( Game.getTime() - ( this.createdTime + FlashExplosion.blowUpTime ) ) / FlashExplosion.fadeAwayTime );

            this.mesh.children[ 0 ].material.opacity = ( 1 - progressFactor ) * FlashExplosion.maxOpacity;
            if( progressFactor === 1 )
                this.phase = 2;
        }
        return this.phase !== 2;

    }

    arcFunc( distance )
    {
        return 0.1 * ( -Math.pow( distance, 2 ) + this.totalDistance * distance );
    }

}

class Grenade extends Entity
{
    static moveSpeed = 0.01;
    static explosionRadius = 0.7;
    static blowUpDelay = 100; //ms
    static initialColor = new THREE.Color( 0.3, 0.2, 0.2 );
    static landedColor = new THREE.Color( 0.7, 0.3, 0.3 );

    constructor( originX, originY, targetX, targetY, createdTime )
    {
        super( originX, originY, createdTime );
        this.phase = 0;
        this.originX = originX;
        this.originY = originY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.deltaX = this.targetX - this.originX;
        this.deltaY = this.targetY - this.originY;
        this.totalDistance = Math.sqrt( Math.pow( this.deltaX, 2 ) + Math.pow( this.deltaY, 2 ) );
    }

    createEntityMesh()
    {
        let entity = new THREE.Group();
        let sphere = new THREE.Mesh(
            new THREE.SphereGeometry( 0.17, 8, 8 ),
            new THREE.MeshPhongMaterial( { color: Grenade.initialColor } )
        );
        entity.add( sphere );
        return entity;
    }

    update()
    {
        if( Game.getTime() < this.createdTime ) return true;
        if( this.phase === 0 )
        {
            let progress = ( Game.getTime() - this.createdTime ) * Grenade.moveSpeed;
            let progressFactor = Math.min( progress / this.totalDistance, 1 );
            this.position.x = this.originX + this.deltaX * progressFactor;
            this.position.y = this.originY + this.deltaY * progressFactor;
            this.position.z = this.arcFunc( progressFactor * this.totalDistance );
            if( progressFactor === 1 )
            {
                this.phase = 1;
                this.landedTime = this.createdTime + this.totalDistance / Grenade.moveSpeed;
            }
        }
        if( this.phase === 1 )
        {
            this.mesh.children[ 0 ].material.color = Grenade.landedColor;
            Game.addEntity( new FlashExplosion( this.targetX, this.targetY, Grenade.explosionRadius, this.landedTime + Grenade.blowUpDelay ) );
            this.phase = 2;
        }
        if( this.phase === 2 && Game.getTime() - this.landedTime > Grenade.blowUpDelay )
        {
            let pos = Game.convertGameToTileCoords( { x: this.targetX, y: this.targetY } );
            Game.gameBoard.setBoardValue( pos, { state: 2, changedTime: this.landedTime + Grenade.blowUpDelay } );
            this.phase = 3;
        }
        return this.phase !== 3;

    }

    arcFunc( distance )
    {
        return 0.1 * ( -Math.pow( distance, 2 ) + this.totalDistance * distance );
    }

}