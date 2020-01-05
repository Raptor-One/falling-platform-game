class UI
{
    static preGameUIPhase = 0;

    static title = document.getElementById("title");
    static titleTimeout = undefined;

    static setTitle( message, time )
    {
        UI.clearTitle();
        UI.title.innerText = message;
        if(time === undefined)
            return;
        UI.titleTimeout = setTimeout( UI.clearTitle, time)
    }

    static clearTitle()
    {
        if( UI.titleTimeout !== undefined)
        {
            clearTimeout( this.titleTimeout );
            UI.titleTimeout = undefined;
        }
        UI.title.innerText = "";
    }

    static updateUI()
    {
        if( UI.preGameUIPhase === 4)
            return;
        if(Game.getTime() > -3000 && UI.preGameUIPhase === 0)
        {
            UI.setTitle( "Game starting in 3..." );
            UI.preGameUIPhase = 1;
        }
        if(Game.getTime() > -2000 && UI.preGameUIPhase === 1)
        {
            UI.setTitle("Game starting in 2...");
            UI.preGameUIPhase = 2;
        }
        if(Game.getTime() > -1000 && UI.preGameUIPhase === 2)
        {
            UI.setTitle("Game starting in 1...");
            UI.preGameUIPhase = 3;
        }
        if(Game.getTime() >= 0 && UI.preGameUIPhase === 3)
        {
            UI.setTitle("Start!", 700);
            UI.preGameUIPhase = 4;
        }
    }
}