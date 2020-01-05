const effects = {
    pushEffect: {
        name: "pushEffect",
        apply: function( entity, params )
        {
            if( params.actual === undefined)
            {
                params.actual = {x: entity.position.x, y: entity.position.y};
                params.actualStartTime = Game.getTime();
            }
            let deltaX = params.target.x - params.actual.x;
            let deltaY = params.target.y - params.actual.y;
            let progress = Math.min((Game.getTime() - params.actualStartTime) / params.duration, 1 );
            entity.position.x = params.actual.x + deltaX * progress;
            entity.position.y = params.actual.y + deltaY * progress;

            if(progress === 1)
            {
                entity.lastPosTime = params.actualStartTime + params.duration;
                entity.lastX = entity.targetX = params.target.x;
                entity.lastY = entity.targetY = params.target.y;
                return false;
            }
            return true;
        }
    }
};