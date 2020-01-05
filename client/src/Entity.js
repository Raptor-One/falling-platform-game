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
        super( x, y, createdTime, { radius: radius } );
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
        this.totalDistance = calcDistance(this.deltaX, this.deltaY);
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

class Wind extends Entity
{
    static moveSpeed = 0.01;
    static radius = 0.3;
    static length = 0.1;
    static startFadeOut = 0.7; //ms
    static initialColor = new THREE.Color( 0.3, 0.2, 0.2 );
    static landedColor = new THREE.Color( 0.7, 0.3, 0.3 );

    constructor( originX, originY, targetX, targetY, createdTime, casterUid )
    {
        super( originX, originY, createdTime );
        this.collider = new Collider(this, rectangleCollider(Wind.radius*2, Wind.length));

        this.casterUid = casterUid;
        this.phase = 0;
        this.originX = originX;
        this.originY = originY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.deltaX = this.targetX - this.originX;
        this.deltaY = this.targetY - this.originY;
        this.totalDistance = calcDistance(this.deltaX, this.deltaY);
        this.totalTime = this.totalDistance / Wind.moveSpeed;
        let decrease = 1 - ( Wind.length * 0.7 ) / this.totalDistance;
        this.deltaX *= decrease;
        this.deltaY *= decrease;
        this.totalDistance *= decrease;
        this.position.z = Wind.radius;
        this.rotation.z = Math.atan( this.deltaY / this.deltaX ) + Math.PI / 2;
    }

    createEntityMesh()
    {
        let entity = new THREE.Group();
        let particleCount = 300;
        let particles = new THREE.Geometry();
        let pMaterial = new THREE.PointsMaterial( {
            color: 0xFFFFFF,
            transparent: true,
            size: 0.01
        } );

        for( let p = 0; p < particleCount; p++ )
        {
            let r = (Math.random() -0.5 ) * Wind.radius *2;
            // let r = Math.random() * Wind.radius / 2 - Wind.radius;
            let l = (Math.random() - 0.5 ) * Wind.length;
            let a = Math.random() * Math.PI * 2;
            let particle = new THREE.Vector3( r * Math.cos( a ), l, r * Math.sin( a ) );
            particles.vertices.push( particle );
        }

        let particleSystem = new THREE.Points(
            particles,
            pMaterial );
        entity.add( particleSystem );
        return entity;
    }

    update()
    {
        if( Game.getTime() < this.createdTime ) return true;
        if( this.phase === 0 || this.phase === 1 )
        {
            let progress = ( Game.getTime() - this.createdTime ) * Wind.moveSpeed;
            let progressFactor = Math.min( progress / this.totalDistance, 1 );
            this.position.x = this.originX + this.deltaX * progressFactor;
            this.position.y = this.originY + this.deltaY * progressFactor;
            this.mesh.children[0].rotation.y += 0.3;
            if( this.phase === 0 && progressFactor > Wind.startFadeOut )
                this.phase = 1;
        }
        if( this.phase === 1 )
        {
            let progressFactor = Math.min( 1, ( Game.getTime() -(  this.createdTime + this.totalTime * Wind.startFadeOut )) / ( this.totalTime * ( 1 - Wind.startFadeOut ) ) );
            this.mesh.children[ 0 ].material.opacity = ( 1 - progressFactor );
            if( progressFactor === 1 )
                this.phase = 2;
        }

        if (this.phase === 2 )
        {
            this.collider.onDelete();
        }

        return this.phase !== 2;
    }

    onCollision( entity, nthis = this )
    {
        if(nthis.casterUid !== gameManager.uid)
            return;

        if( entity instanceof Player && entity.uid !== nthis.casterUid)
        {
            let distance = 1.2;
            let duration = 200;
            let direction = Math.atan(nthis.deltaY/nthis.deltaX);
            if( nthis.deltaX < 0) direction += Math.PI;
            let origin = {x: entity.position.x, y:entity.position.y};
            let target = {x: origin.x + distance * Math.cos(direction), y: origin.y + distance * Math.sin(direction)};
            let params = {
                origin: origin,
                target: target,
                duration: duration,
                startTime: Game.getTime()
            };
            gameManager.triggerEffect( "pushEffect", entity.uid, params, gameManager );
        }
    }
}