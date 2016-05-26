package vs.malte.services;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class GamesServiceAPI
{
    private final GamesService gameService;

    public GamesServiceAPI( )
    {
//        this.gameService = gameService;
        this.gameService = new GamesService();
        
        initGetAvailableGames();
        initGetGame();
        initGetGameServices();
        initGetGameComponents();
        initGetAllPlayers();
        initGetPlayerReadyness();
        initGetSpecificPlayer();
        initGetGameStatus();
        initGetPlayersTurn();
        initGetPlayersCurrent();

        initPostNewGame();
        initPostNewPlayer();

        initPutGameService();
        initPutGameComponent();
        initPutPlayerReady();
        initPutGameStatus();
        initPutPlayersTurn();
    }

    /**
     * Initialisiert die Post-Methode zum erstellen eines neuen Spiels
     */
    private void initPostNewGame()
    {
        post( "/games", ( req, resp ) ->
        {
            return gameService.postNewGame( req, resp );
        } );
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der aktuellen Spiele
     */
    private void initGetAvailableGames()
    {
        get( "/games", ( req, resp ) ->
        {
            return gameService.getAvailableGames( req, resp );
        } );
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage des aktuellen Spielstatus
     */
    private void initGetGame()
    {
        get( "/games/:gameid", ( req, resp ) ->
        {
            return gameService.getGame( req, resp );
        } );
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der im Spiel verwendeten Services
     */
    private void initGetGameServices()
    {
        get( "/games/:gameid/services", ( req, resp ) ->
        {
            return gameService.getGameServices( req, resp );
        } );
    }

    /**
     * Initialisiert die PUT-Methode zum setzen eines Services fuer ein Spiel.
     */
    private void initPutGameService()
    {
        put( "/games/:gameid/services", ( req, resp ) ->
        {
            return gameService.putGameService( req, resp );
        } );
    }

    /**
     * Initialisiert die PUT-Methode zum setzen von ein oder mehrerer Komponenten fuer ein Spiel.
     */
    private void initPutGameComponent()
    {
        put( "/games/:gameid/components", ( req, resp ) ->
        {
            return gameService.putGameComponent( req, resp );
        } );
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der im Spiel verwendeten Services
     */
    private void initGetGameComponents()
    {
        get( "/games/:gameid/components", ( req, resp ) ->
        {
            return gameService.getGameComponents( req, resp );
        } );
    }

    /**
     * Initialisiert die Post-Methode zum erstellen eines neuen Spielers. Greift auf Userservice zu und Speichert den Spieler dort (kA wozu TODO).
     * 
     * return nur Statuscodes ( 201 falls Spieler erfolgreich erstellt wurde; 409 falls Spieler bereits vorhanden )
     */
    private void initPostNewPlayer()
    {
        post( "/games/:gameId/players", ( req, resp ) ->
        {
            return gameService.postNewPlayer( req, resp );
        } );
    }

    /**
     * Initialisiert die GET-Methode zum Erfragen der Bereitschaft eines Spielers.
     */
    private void initGetAllPlayers()
    {
        get( "/games/:gameId/players", ( req, resp ) ->
        {
            return gameService.getAllPlayers( req, resp );
        } );
    }

    /**
     * Initialisiert die GET-Methode zum Erfragen der Bereitschaft eines Spielers.
     */
    private void initGetPlayerReadyness()
    {
        get( "/games/:gameId/players/:playerId/ready", ( req, resp ) ->
        {
            return gameService.getPlayerReadyness( req, resp );
        } );
    }

    /**
     * Initialisiert die PUT-Methode zum Aendern der Bereitschaft eines Spielers.
     */
    private void initPutPlayerReady()
    {
        put( "/games/:gameid/players/:playerid/ready", ( req, resp ) ->
        {
            return gameService.putPlayerReady( req, resp );
        } );
    }

    private void initGetSpecificPlayer()
    {
        get( "/games/:gameId/players/:playerId", ( req, resp ) ->
        {
            return gameService.getSpecificPlayer( req, resp );
        } );
    }

    private void initGetGameStatus()
    {
        get( "/games/:gameId/status", ( req, resp ) ->
        {
            return gameService.getGameStatus( req, resp );
        } );
    }

    /**
     * Veraendert bei Aufruf, entsprechend der vorliegenden Daten, den Status des Spiels. Standardwert: "registration", nach Erstellen des Spiels. Das Aufrufen der put-Methode
     * bewirkt erst eine Veraenderung, wenn der Status aller Spieler auf "ready" geaendert wurde, dann aendert sich bei Aufruf der Status auf "running". Sobald alle Kriterien
     * erfuellt sind, die das Spiel laut Regeln beenden, bewirkt das erneute Aufrufen der put-Methode eine Aenderung des Statuses in "finished".
     * 
     * return nur Statuscodes ( 200: Status geaendert; 409: Status konnte nicht geaendert werden )
     */
    private void initPutGameStatus()
    {
        put( "/games/:gameId/status", ( req, resp ) ->
        {
            return gameService.putGameStatus( req, resp );
        } );
    }

    /**
     * Versucht den Mutex fuer den jeweiligen Spieler zuerhalten. Klappt nur wenn dieser auch an der Reihe ist.
     * 
     * Uebergeben werden muss ein Playerobjekt als json. ( Die Methode verwendet nur die ID des Playerobjekts, die anderen Felder koennen leer bleiben )
     * 
     */
    private void initPutPlayersTurn()
    {
        put( "/games/:gameId/player/turn", ( req, resp ) ->
        {
            return gameService.putPlayersTurn( req, resp );
        } );
    }

    private void initGetPlayersTurn()
    {
        get( "/games/:gameId/player/turn", ( req, resp ) ->
        {
            return gameService.getPlayersTurn( req, resp );
        } );
    }

    private void initGetPlayersCurrent()
    {
        get( "/games/:gameId/player/current", ( req, resp ) ->
        {
            return gameService.getPlayersCurrent( req, resp );
        } );
    }
}
