
function calcDistance( deltaX, deltaY )
{
    return Math.sqrt( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) )
}

Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

/**
 * returns grid square of point that blocks LOS
 */
function getLOSBlockingPoint( lineStart, lineEnd, grid, condition )
{
    let xSign = lineEnd.x > lineStart.x ? 1 : -1;
    let ySign = lineEnd.y > lineStart.y ? 1 : -1;

    let deltaX = lineEnd.x - lineStart.x + xSign;
    let deltaY = lineEnd.y - lineStart.y + ySign;
    let slope = deltaY / deltaX;
    let absSlope = Math.min( Math.abs( slope ), Math.abs( deltaY ) );
    let xSlope = deltaX / deltaY;
    let absXSlope = Math.min( Math.abs( xSlope ), Math.abs( deltaX ) );
    console.log( absSlope );

    let start = Game.convertGameToTileCoords( lineStart );

    for( let x = 0; x < Math.ceil( Math.abs( deltaX ) ); x++ )
    {
        let cumulativeY = absSlope * x;
        for( let y = Math.floor( cumulativeY ); y < Math.ceil( cumulativeY + absSlope ); y++ )
        {
            if( cumulativeY + absSlope > deltaY * ySign )
            {
                return lineEnd;
            }
            let pos = { x: start.x + x * xSign, y: start.y + y * ySign };
            if( grid[ pos.x ] === undefined || grid[ pos.x ][ pos.y ] === undefined || condition( grid[ pos.x ][ pos.y ].state ) )
            {
                return calculateCollisionPoint( pos, xSign, ySign, absSlope, lineStart, absXSlope );
                // Game.gameBoard.setBoardValue(pos, {state: 2, changedTime:Game.getTime()});
            }
        }
    }

    return lineEnd;
}

function calculateCollisionPoint( pos, xSign, ySign, absSlope, lineStart, absXSlope )
{
    let intersect = Game.convertTileToGameCoords( pos );
    if( xSign > 0 )
        intersect.x -= 0.8;
    else
        intersect.x += 0.8;
    if( ySign > 0 )
        intersect.y -= 0.8;
    else
        intersect.y += 0.8;

    let newY = ( ySign * xSign * absSlope ) * ( intersect.x - lineStart.x ) + lineStart.y;
    let invalid = false;
    if( ySign > 0 )
    {
        if( newY < intersect.y )
            invalid = true;
    } else
    {
        if( newY > intersect.y )
            invalid = true;
    }
    if( invalid )
    {
        let newX = ( ySign * xSign * absXSlope ) * ( intersect.y - lineStart.y ) + lineStart.x;
        intersect.x = newX;
    } else
        intersect.y = newY;

    return intersect;
}
