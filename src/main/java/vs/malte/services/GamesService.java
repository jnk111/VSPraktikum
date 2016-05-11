package vs.malte.services;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.malte.models.Components;
import vs.malte.models.CreateUserDTO;
import vs.malte.models.Game;
import vs.malte.models.GameDTO;
import vs.malte.models.ServiceArray;
import vs.malte.models.ServiceDTO;
import vs.malte.models.ServiceList;

public class GamesService
{
    private final String GAMEID_PREFIX = "/games/";
    private final String USERID_PREFIX = "/users/";
    private final String YELLOW_PAGE = "http://172.18.0.5:4567";
    private final String YP_GROUP_CMD = "/services/of/name/";
    private final String YP_GROUP_NAME = "JJMG";

    private static Map<String, Game> games;

    public GamesService()
    {
        games = new HashMap<>();

        initPostNewGame();
        initGetAvailableGames();
        initGetGameStatus();
        initGetGameServices();
        initPutGameService();
        initPutGameComponent();
        initGetGameComponents();
        initPostNewPlayer();
        initGetAllPlayers();
        initGetPlayerReadyness();
        initPutPlayerReady();
    }

    /**
     * Initialisiert die Post-Methode zum erstellen eines neuen Spiels
     */
    private void initPostNewGame()
    {
        post( "/games", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            Game game = createGame( req.body() );   // TODO Fehlerbehandlung: kein Game name angegeben
            game = initGameServices( game );
            game = initGameComponents( game );

            if ( game != null && game.isValid() )
            {
                if ( !games.containsKey( game.getId() ) )
                {
                    games.put( game.getId(), game );

                    resp.status( 201 ); // Created
                }
                else
                {
                    resp.status( 409 ); // Conflict
                }
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }

            return "";
        } );
    }

    private Game createGame( String body )
    {
        GameDTO gameDTO = new Gson().fromJson( body, GameDTO.class );
        Game newGame = new Game();
        newGame.setName( gameDTO.getName() );
        newGame.setId( GAMEID_PREFIX + gameDTO.getName() );

        return newGame;
    }

    private Game initGameServices( Game newGame )
    {
        Map<String, String> newServices = new HashMap<>();

        try
        {
            HttpResponse<JsonNode> response = Unirest.get( YELLOW_PAGE + YP_GROUP_CMD + YP_GROUP_NAME ).asJson();
            ServiceArray servicesOfGroup = new Gson().fromJson( response.getBody().toString(), ServiceArray.class );

            for ( String s : servicesOfGroup.getServices() )
            {
                response = Unirest.get( YELLOW_PAGE + s ).asJson();
                ServiceDTO serviceDTO = new Gson().fromJson( response.getBody().toString(), ServiceDTO.class );
                newServices.put( serviceDTO.getService(), serviceDTO.getUri() );
            }

            newGame.getServiceList().setAllServices( newServices );
            newGame.setPlayers( newServices.get( "users" ) ); // TODO: Userservice ( Da Userservice laut Spezi nicht unter Services gespeichert ist )
        }
        catch ( UnirestException e )
        {
            e.printStackTrace();
        }

        return newGame;
    }

    /**
     * TODO Components
     * 
     * @param game
     * @return
     */
    private Game initGameComponents( Game game )
    {
        // Init Board
        String boardsUri = game.getServiceList().getBoard();

        game.getComponents().setBoard( game.getServiceList().getBoard() );

        return game;
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der aktuellen Spiele
     */
    private void initGetAvailableGames()
    {
        get( "/games", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            StringBuffer gameList = new StringBuffer();
            Gson gson = new Gson();

            resp.status( 200 );

            if ( !games.isEmpty() )
            {
                for ( Game game : games.values() )
                    gameList.append( gson.toJson( game ) + "\n" );
            }
            else
            {
                resp.status( 204 ); // No Content
            }

            return gameList.toString();
        } );
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage des aktuellen Spielstatus
     */
    private void initGetGameStatus()
    {
        get( "/games/:gameid", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 );
            String result = "";

            Game game = getGame( req.params( ":gameId" ) );

            if ( game != null )
            {
                resp.status( 200 ); // Created

                result = new Gson().toJson( game );
            }
            else
            {
                resp.status( 404 ); // Resource could not be found
            }

            return result;
        } );
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der im Spiel verwendeten Services
     */
    private void initGetGameServices()
    {
        get( "/games/:gameid/services", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 );
            String result = "";

            Game game = getGame( req.params( ":gameId" ) );

            if ( game != null )
            {
                resp.status( 200 ); // Created

                result = new Gson().toJson( game.getServiceList() );
            }
            else
            {
                resp.status( 404 ); // Resource could not be found
            }

            return result;
        } );
    }

    /**
     * Initialisiert die PUT-Methode zum setzen eines Services fuer ein Spiel.
     */
    private void initPutGameService()
    {
        put( "/games/:gameid/services", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            ServiceList newServiceList = new Gson().fromJson( req.body(), ServiceList.class );
            Game game = getGame( req.params( ":gameId" ) );

            if ( game != null )
            {
                resp.status( 202 ); // Accepted

                game.getServiceList().setAllServices( newServiceList.getAllServices() );
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }
            return "";
        } );
    }

    /**
     * Initialisiert die PUT-Methode zum setzen von ein oder mehrerer Komponenten fuer ein Spiel.
     */
    private void initPutGameComponent()
    {
        put( "/games/:gameid/components", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            Components newComponents = new Gson().fromJson( req.body(), Components.class );
            Game game = getGame( req.params( ":gameId" ) );

            if ( game != null )
            {
                resp.status( 202 ); // Accepted

                game.getComponents().setAllComponents( newComponents.getAllComponents() );
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }
            return "";
        } );
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der im Spiel verwendeten Services
     */
    private void initGetGameComponents()
    {
        get( "/games/:gameid/components", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 );
            String result = "";
            Game game = getGame( req.params( ":gameId" ) );

            if ( game != null )
            {
                resp.status( 200 ); // Created

                result = new Gson().toJson( game.getComponents().getAllComponents() );
            }
            else
            {
                resp.status( 404 ); // Resource could not be found
            }

            return result;
        } );
    }

    /**
     * Initialisiert die Post-Methode zum erstellen eines neuen Spielers. Greift auf Userservice zu um Spieler zu speichern.
     */
    private void initPostNewPlayer()
    {
        post( "/games/:gameId/players", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            Game game = getGame( req.params( ":gameId" ) );
            String hostUri = req.host();                                                           // TODO: Host-URI so richtig???

            Player newPlayer = new Gson().fromJson( req.body(), Player.class );                   // Erstellt Playerobjekt mit Namen

            newPlayer.setId( "/users/" + newPlayer.getUser().toLowerCase() );                      // TODO: ID so richtig? oder komplette URL?
            newPlayer.setUser( newPlayer.getUser().toLowerCase() );

            if ( game != null && !game.getPlayers().containsKey( newPlayer.getId() ) )
            {
                CreateUserDTO userDTO = new CreateUserDTO();
                userDTO.setId( newPlayer.getId() );
                userDTO.setName( newPlayer.getUser().toLowerCase() );
                userDTO.setUri( hostUri );

                HttpService.post( game.getUserService(), userDTO );

                game.getPlayers().put( newPlayer.getId(), newPlayer );

                resp.status( 201 ); // created
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }

            return "";
        } );
    }

    /**
     * Initialisiert die GET-Methode zum Erfragen der Bereitschaft eines Spielers.
     */
    private void initGetAllPlayers()
    {
        get( "/games/:gameId/players", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            String result = "";
            Game game = getGame( req.params( ":gameId" ) );

            if ( game != null )
            {
                resp.status( 200 ); // OK

                result = new Gson().toJson( game.getPlayers() );
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }

            return result;
        } );
    }

    /**
     * Initialisiert die GET-Methode zum Erfragen der Bereitschaft eines Spielers.
     */
    private void initGetPlayerReadyness()
    {
        get( "/games/:gameId/players/:playerId/ready", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            String result = "";
            Game game = getGame( req.params( ":gameId" ) );
            String playerId = ( USERID_PREFIX + req.params( ":playerId" ) );

            Player player = game.getPlayers().get( playerId );

            if ( player != null )
            {
                resp.status( 200 ); // created

                result = new Gson().toJson( player.getReadyness() );
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }
            
            return result;
        } );
    }

    /**
     * Initialisiert die GET-Methode zum Erfragen der Bereitschaft eines Spielers.
     */
    private void initPutPlayerReady()
    {
        put( "/games/:gameid/players/:playerid/ready", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            String playerId = ( USERID_PREFIX + req.params( ":playerId" ) );
            Game game = getGame( req.params( ":gameId" ) );

            if ( game != null )
            {
                Player player = game.getPlayers().get( playerId );

                if ( player != null )
                {
                    resp.status( 200 ); // created

                    player.setReady( true );
                }
                else
                {
                    resp.status( 400 ); // Bad Request
                }
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }
            return "";
        } );
    }

    private Game getGame( String gameId )
    {
        return games.get( GAMEID_PREFIX + gameId );
    }
}
