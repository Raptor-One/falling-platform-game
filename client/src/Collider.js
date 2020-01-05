const circleCollider = function( radius )
{
    return function( t )
    {
        return radius;
    };
};
const rectangleCollider = function( width, height, angle = 0 )
{
    width /= 2;
    height /= 2;
    return function( t )
    {
        t += angle;
        if( Math.abs( Math.tan( t ) ) < height / width )
        {
            return width / Math.abs( Math.cos( t ) );
        } else
        {
            return height / Math.abs( Math.sin( t ) );
        }
    };
};

class Collider
{
    static pMaterial = new THREE.PointsMaterial( {
        color: 0x00FF00,
        visible: true,
        size: 0.01
    } );

    static setDebugVisible( isVisible )
    {
        Collider.pMaterial.visible = isVisible;
    }

    collidedWith = {};

    constructor( entity, colliderFunction, debugParticleCount = 300 )
    {
        this.entity = entity;
        this.colliderFunction = colliderFunction;
        if( entity.onCollision === undefined)
            entity.onCollision = function() {};

        let particles = new THREE.Geometry();

        for( let p = 0; p < debugParticleCount; p++ )
        {
            let t = p / debugParticleCount * Math.PI * 2;
            let r = colliderFunction( t );
            let particle = new THREE.Vector3( r * Math.cos( t ), r * Math.sin( t ), 0 );
            particles.vertices.push( particle );
        }

        let particleSystem = new THREE.Points(
            particles,
            Collider.pMaterial );
        entity.add( particleSystem );

        Game.colliderManager.colliders.push(this);
    }

    currentlyCollided( entity, updateNumber )
    {
        let lastCollided = this.collidedWith[entity];
        this.collidedWith[entity] = updateNumber;
        if(lastCollided !== undefined && lastCollided + 1 === updateNumber)
            return;
        this.entity.onCollision( entity, this.entity )
    }

    onDelete()
    {
        Game.colliderManager.colliders.remove( this );
    }
}

class ColliderManager
{
    colliders = [];
    updateNumber = 0;

    checkForCollisions()
    {
        let nthis = this;
        this.colliders.forEach( function( value, index )
        {
            for( let i = index + 1; i < nthis.colliders.length; i++ )
            {
                let other = nthis.colliders[ i ];
                let deltaX = value.entity.position.x - other.entity.position.x;
                let deltaY = value.entity.position.y - other.entity.position.y;
                let distance = calcDistance( deltaX, deltaY );
                let direction = Math.atan( deltaY / deltaX );
                if( distance <= value.colliderFunction( direction ) + other.colliderFunction( direction ) )
                {
                    value.currentlyCollided( other.entity, nthis.updateNumber );
                    other.currentlyCollided( value.entity, nthis.updateNumber );
                }
                // console.log( value.toString()  + " " + distance)
            }
        } );
        this.updateNumber++;
    }

}