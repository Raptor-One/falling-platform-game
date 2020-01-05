const normalColor = new THREE.Color( 0, 0.3, 0.5 );
const disintegratingColor = new THREE.Color( 1, 0.4, 0 );
const fallingColor = new THREE.Color( 1, 0.1, 0 );
const disintegrateWaitTime = 500; //ms
const disintegratePhaseTime = 500; //ms
const disintegrateOpacityMin = 0.4; //%
const fallingPhaseWaitTime = 200; //ms
const fallingPhaseTime = 700; //ms
const fallDistance = -3; //units

class GameBoard extends THREE.Group
{
    constructor( width, height )
    {
        super();
        this.width = width;
        this.height = height;
        this.platform = [];
        this.inTransition = [];
        for( let x = 0; x < width; x++ )
        {
            let column = [];
            for( let y = 0; y < height; y++ )
            {
                let boardPiece = this.createBoardPiece( x, y );
                super.add( boardPiece );
                column.push(
                    {
                        state: 1,
                        changedTime: 0,
                        boardPiece: boardPiece,
                        phase: 0
                    }
                );
            }
            this.platform.push( column );
        }
        this.position.z = -0.1;
    }

    updateGraphics()
    {
        for( let i = 0; i < this.inTransition.length; i++ )
        {
            let pos = this.inTransition[ i ];
            let tile = this.platform[ pos.x ][ pos.y ];
            let transitionDone = false;
            let mesh = tile.boardPiece.children[ 0 ];
            let deltaTime = Game.getTime() - tile.changedTime;
            if( deltaTime < 0)
                continue;
            switch( tile.state )
            {
                case 0:
                    super.remove( tile.boardPiece );
                    transitionDone = true;
                    break;
                case 1:
                    let boardPiece = this.createBoardPiece( pos.x, pos.y );
                    super.add( boardPiece );
                    tile.boardPiece = boardPiece;
                    transitionDone = true;
                    break;
                case 2:
                    if( tile.phase === 0 )
                    {
                        mesh.material.color = disintegratingColor;
                        tile.phase = 1;
                    }
                    if( tile.phase === 1 && deltaTime > disintegrateWaitTime )
                    {
                        mesh.material.opacity = 1;
                        mesh.material.transparent = true;
                        tile.phase = 2;
                    }
                    if( tile.phase === 2 )
                    {
                        let factor = ( deltaTime - disintegrateWaitTime ) / disintegratePhaseTime;
                        mesh.material.opacity = 1 - factor * ( 1 - disintegrateOpacityMin );
                        if( mesh.material.opacity <= disintegrateOpacityMin )
                            setTileState(tile, 3, disintegrateWaitTime + disintegratePhaseTime)
                    }
                    break;
                case 3:
                    if( tile.phase === 0 )
                    {
                        mesh.material.color = fallingColor;
                        mesh.material.opacity = disintegrateOpacityMin;
                        mesh.material.transparent = true;
                        tile.phase = 1;
                    }

                    let fallFactor = deltaTime / ( fallingPhaseWaitTime + fallingPhaseTime );
                    tile.boardPiece.position.z = fallDistance * fallFactor;

                    if( tile.phase === 1 && deltaTime > fallingPhaseWaitTime )
                    {
                        let phaseFactor = ( deltaTime - fallingPhaseWaitTime ) / fallingPhaseTime;
                        mesh.material.opacity = disintegrateOpacityMin - phaseFactor * disintegrateOpacityMin;
                        if( mesh.material.opacity <= 0 )
                            setTileState(tile, 0, fallingPhaseWaitTime + fallingPhaseTime)
                    }
                    break;
                default:
                    console.error( `Tile (${pos.x}, ${pos.y}) has invalid state of ${tile.state}` );
            }
            if( transitionDone )
                this.inTransition.splice( i--, 1 );
        }

        function setTileState( tile, state, nextPhaseTimeOffset )
        {
            tile.phase = 0;
            tile.state = state;
            tile.changedTime += nextPhaseTimeOffset;
        }
    }

    setBoardValue( pos, value )
    {
        if( value.state !== this.platform[ pos.x ][ pos.y ].state )
        {
            this.inTransition.push( pos );
            let tile = this.platform[ pos.x ][ pos.y ];
            tile.state = value.state;
            tile.changedTime = value.changedTime;
            tile.phase = 0;
        } else
        {
            console.warn( "Update received with no state change, server and client may be out-of-sync" );
        }
    }

    createBoardPiece( x, y )
    {
        let boardPiece = new THREE.Group();

        let box = new THREE.Mesh(
            new THREE.BoxGeometry( 0.95, 0.95, 0.2 ),
            new THREE.MeshPhongMaterial( { color: normalColor, reflectivity: 0.5, flatShading: true } )
        );

        boardPiece.add( box );

        boardPiece.position.x = x - this.width / 2 + 0.5;
        boardPiece.position.y = y - this.height / 2 + 0.5;

        return boardPiece;
    }
}