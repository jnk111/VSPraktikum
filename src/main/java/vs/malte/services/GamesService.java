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

import vs.malte.json.BankDTO;
import vs.malte.json.BoardDTO;
import vs.malte.json.BrokerDTO;
import vs.malte.json.CreateUserDTO;
import vs.malte.json.DecksDTO;
import vs.malte.json.GameDTO;
import vs.malte.json.PawnDTO;
import vs.malte.json.PlayerDTO;
import vs.malte.json.ServiceArray;
import vs.malte.json.ServiceDTO;
import vs.malte.models.Components;
import vs.malte.models.Game;
import vs.malte.models.Player;
import vs.malte.models.ServiceList;

public class GamesService
{
    private final String GAMEID_PREFIX = "/games/";
    private final String USERID_PREFIX = "/users/";
    private final String YELLOW_PAGE = "http://172.18.0.5:4567";
    private final String YP_GROUP_CMD = "/services/of/name/";
    private final String YP_GROUP_NAME = "JJMG";

    private final boolean LOCAL = true;     // Zu Testzwecken: LOCAL auf true, wenn alle Services lokal laufen

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
        initGetSpecificPlayer();
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
            
            if ( game != null && game.isValid() )
            {
                if ( !games.containsKey( game.getId() ) )
                {
                    game = initGameServices( game );
                    game = initGameComponents( game );
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

        if ( !LOCAL )
        {
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
                e.printStackTrace();    // TODO: Fehlerbehanldung
            }
        }
        else
        {
            // ======================== Manuelle Service-Einstellungen ======================== //

            newServices.put( "dice", "http://localhost:4567/dice" );
            newServices.put( "board", "http://localhost:4567/boards" );
            newServices.put( "bank", "http://localhost:4567/banks" );
            newServices.put( "broker", "http://localhost:4567/broker" );
            newServices.put( "decks", "http://localhost:4567/decks" );
            newServices.put( "events", "http://localhost:4567/events" );
            newServices.put( "users", "http://localhost:4567/users" );

            newGame.getServiceList().setAllServices( newServices );
            newGame.setPlayers( newServices.get( "users" ) );
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
        game = createBoard( game );
        game = createBank( game );
        game = createBroker( game );
        game = createDecks( game );

        // initialize dice
        game.getComponents().setDice( game.getServiceList().getDice() );

        // initialize events
        game.getComponents().setEvents( game.getServiceList().getEvents() );

        return game;
    }

    private Game createBoard( Game game )
    {
        String boardsUrl = game.getServiceList().getBoard();
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setGame( game.getId() );

        int responseCode = HttpService.post( boardsUrl, boardDTO );

        if ( responseCode == 200 )
        {    
            game.getComponents().setBoard( boardsUrl + "/" + game.getName() );
            
            System.out.println( "Board created: " + game.getComponents().getBoard() );
        }
        else
        {
            System.err.println( "Fehler beim erstellen eines Boards" );
            // TODO throw Component not available Exception
        }

        return game;
    }

    private Game createBank( Game game )
    {
        String bankUrl = game.getServiceList().getBank();
        BankDTO bankDTO = new BankDTO();
        bankDTO.setGame( game.getId() );

        int responseCode = HttpService.post( bankUrl, bankUrl );

        if ( responseCode == 200 )
        {
            game.getComponents().setBank( bankUrl + "/" + game.getName() );
        }
        else
        {
            // TODO throw Component not available Exception
        }

        return game;
    }

    private Game createBroker( Game game )
    {
        String brokerUrl = game.getServiceList().getBank();
        BrokerDTO brokerDTO = new BrokerDTO();
        brokerDTO.setGame( game.getId() );

        int responseCode = HttpService.post( brokerUrl, brokerUrl );

        if ( responseCode == 200 )
        {
            game.getComponents().setBroker( brokerUrl + "/" + game.getName() );
        }
        else
        {
            // TODO throw Component not available Exception
        }

        return game;
    }

    private Game createDecks( Game game )
    {
        String decksUrl = game.getServiceList().getBank();
        DecksDTO decksDTO = new DecksDTO();
        decksDTO.setGame( game.getId() );

        int responseCode = HttpService.post( decksUrl, decksUrl );

        if ( responseCode == 200 )
        {
            game.getComponents().setDecks( decksUrl + "/" + game.getName() );
        }
        else
        {
            // TODO throw Component not available Exception
        }

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
            String mapKey = newPlayer.getUserName();

            // ================= Playerobjekt wird entsprechend der Spezi fuer GameService konfiguriert ================= //
            newPlayer.setId( "/games/" + game.getName() + "/players/" + newPlayer.getUserName().toLowerCase() );
            newPlayer.setUserName( "/user/" + newPlayer.getUserName().toLowerCase() );

            if ( game != null && !game.getPlayers().containsKey( mapKey ) )
            {
                // ================= Post an UserService (User wird im UserService erstellt) ================= //

                CreateUserDTO userDTO = new CreateUserDTO();
                userDTO.setId( newPlayer.getUserName().replaceAll( "/user/", "/users/" ) );
                userDTO.setName( newPlayer.getUserName().replaceAll( "user/", "" ) );
                userDTO.setUri( hostUri + "/client/" + userDTO.getName() );

                System.out.println( "userDTO: " + new Gson().toJson( userDTO ) );

                HttpService.post( game.getUserService(), userDTO );

                createPawn( newPlayer, game );

                game.getPlayers().put( mapKey , newPlayer );

                resp.status( 201 ); // created
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }

            return "";
        } );
    }

    private Player createPawn( Player player, Game game )
    {
        PawnDTO newPawn = new PawnDTO();

        newPawn.setPlayer( player.getId() );
        newPawn.setPlace( "/boards/" + game.getName() + "/places/" + "0" );   // TODO muss automatisiert werden, aber wie? Wer "putet", gameservice oder boards?
        newPawn.setPosition( 0 );                                             // TODO SAME SAME

        System.out.println( "Pawn: " + new Gson().toJson( newPawn ) );
        System.out.println( "playerPawnUri: " + game.getComponents().getBoard() + "/pawns/" + player.getUserName().replaceAll( "user/", "" ) );

        int responseCode = HttpService.post( game.getComponents().getBoard() + "/pawns", newPawn );

        if ( responseCode == 200 )
        {
            player.setPawn( game.getComponents().getBoard() + "/pawns/" + player.getUserName().replaceAll( "user/", "" ) );
        }
        else
        {
            System.err.println( "Pawn erstellen fehlgeschlagen" );      // TODO Fehlerbehandlung falls Pawn nicht erstellt werden kann (Neuversuch alle 3s?).
        }

        return player;
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
                PlayerDTO players = new PlayerDTO();

                for ( Player player : game.getPlayers().values() )
                {
                    players.getPlayers().add( "/games/" + game.getName() + "/players" + player.getUserName().replaceAll( "/user", "") );
                }

                result = new Gson().toJson( players );
                resp.status( 200 ); // OK
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

    private void initGetSpecificPlayer()
    {
        get( "/games/:gameId/players/:playerId", ( req, resp ) ->
        {
            resp.header( "Content-Type", "application/json" );
            resp.status( 500 ); // Internal Server Error

            String result = "";
            Game game = getGame( req.params( ":gameId" ) );
            String mapKey = ( req.params( ":playerId" ).toLowerCase() );
            
            Player player = game.getPlayers().get( mapKey );

            if ( player != null )
            {
                resp.status( 200 ); // created

                result = new Gson().toJson( player );
            }
            else
            {
                resp.status( 400 ); // Bad Request
            }

            return result;
        } );
    }

    private Game getGame( String gameId )
    {
        return games.get( GAMEID_PREFIX + gameId );
    }
}
