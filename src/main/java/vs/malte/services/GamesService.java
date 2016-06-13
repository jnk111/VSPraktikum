package vs.malte.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import spark.Request;
import spark.Response;
import vs.malte.json.BankDTO;
import vs.malte.json.CreateBoardDTO;
import vs.malte.json.BrokerDTO;
import vs.malte.json.CreateGameDTO;
import vs.malte.json.CreateUserDTO;
import vs.malte.json.CurrentPlayerDTO;
import vs.malte.json.DecksDTO;
import vs.malte.json.GameDTO;
import vs.malte.json.GamesListDTO;
import vs.malte.json.InitBoardDTO;
import vs.malte.json.NewAccountDTO;
import vs.malte.json.PawnDTO;
import vs.malte.json.AllPlayersArrayDTO;
import vs.malte.json.AllPlayersDTO;
import vs.malte.json.ServiceArray;
import vs.malte.json.ServiceDTO;
import vs.malte.json.SpecificPlayerDTO;
import vs.malte.models.Components;
import vs.malte.models.Game;
import vs.malte.models.Player;
import vs.malte.models.ServiceList;

public class GamesService
{
    // ************************CODE CONFIGS************************ //

    private final boolean DEBUG_MODE = true; // Zu Testzwecken: Konsolenausgaben aktivieren
    private final boolean LOCAL = true;      // Zu Testzwecken: LOCAL auf true, wenn alle Services lokal laufen sollen

    // **************************PREFIXES************************** //

    private final String GAMEID_PREFIX = "/games/";
    private final String PLAYERID_INFIX = "/players/";
    private final String USER_NAME_PREFIX = "/users/";
    private final String ID_PREFIX_FOR_INIT_BOARDS = "/boards/";

    // ********************YELLOW PAGE CONFIGS********************* //

    private final String YELLOW_PAGE = "http://172.18.0.5:4567";
    private final String YP_GROUP_CMD = "/services/of/name/";
    private final String YP_GROUP_NAME = "JJMG";

    // ***********************RESPONSE CODES*********************** //

    private final int CREATE_GAME_RESP_CODE = 201;
    private final int CREATE_BOARD_RESP_CODE = 200;
    private final int INIT_BOARD_RESP_CODE = 200;

    private final int INVALID_PARAMS_RESP_CODE = 400;
    private final int GAME_ALREADY_EXCITS_RESP_CODE = 409;
    private final int SERVER_ERR_RESP_CODE = 500;

    // ************************START SALDO************************ //

    private final int START_SALDO = 20000;

    // ********************************************************** //
    private Map<String, Game> games;
    private final MutexService mutexService;

    public GamesService()
    {
        games = new HashMap<>();
        mutexService = new MutexService();
    }

    /**
     * Initialisiert die Post-Methode zum erstellen eines neuen Spiels
     * 
     * URI: /games
     */
    public synchronized String postNewGame( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( SERVER_ERR_RESP_CODE ); // Internal Server Error

        Game game = createGame( req.body() );

        if ( game != null && game.isValid() )
        {
            if ( !games.containsKey( game.getId().toLowerCase() ) )
            {
                this.games.put( game.getId().toLowerCase(), game );

                game = initGameServices( game );
                game = initGameComponents( game );

                resp.status( CREATE_GAME_RESP_CODE ); // Created: Spiel erstellt
            }
            else
            {
                if ( DEBUG_MODE )
                {
                    System.out.println( "Game erstellen fehlgeschlagen: Game bereits vorhanden." );
                }
                resp.status( GAME_ALREADY_EXCITS_RESP_CODE ); // Conflict: Spiel mit selben Namen bereits vorhanden
            }
        }
        else
        {
            if ( DEBUG_MODE )
                System.out.println( "Game erstellen fehlgeschlagen" );

            resp.status( INVALID_PARAMS_RESP_CODE ); // Bad Request: ungueltige Eingaben
        }

        return "";
    }

    private Game createGame( String body )
    {
        CreateGameDTO gameDTO = new Gson().fromJson( body, CreateGameDTO.class );

        Game newGame = null;

        if ( gameDTO.isValid() )
        {
            if ( DEBUG_MODE )
            {
                System.out.println( "********************CREATE GAME********************" );
                System.out.println( "NAME: " + gameDTO.getName() );
            }

            newGame = new Game();

            newGame.setName( gameDTO.getName() );
            newGame.setId( GAMEID_PREFIX + gameDTO.getName() );
            mutexService.newGameMutex( newGame.getId() );   // Erstelle Mutex
        }

        return newGame;
    }

    private Game initGameServices( Game newGame )
    {
        Map<String, String> newServices = new HashMap<>();  // Map zum Speichern und anschliessendem Uebertragen in die ServiceList des Games

        if ( !LOCAL )
        {
            try
            {
                HttpResponse<JsonNode> response = Unirest.get( YELLOW_PAGE + YP_GROUP_CMD + YP_GROUP_NAME ).asJson();       // Service-Adressen von YP holen
                ServiceArray servicesOfGroup = new Gson().fromJson( response.getBody().toString(), ServiceArray.class );    // Service-Adressen in Array schreiben

                for ( String service : servicesOfGroup.getServices() )
                {
                    response = Unirest.get( YELLOW_PAGE + service ).asJson();
                    ServiceDTO serviceDTO = new Gson().fromJson( response.getBody().toString(), ServiceDTO.class );
                    newServices.put( serviceDTO.getService(), serviceDTO.getUri() );
                }

                newGame.getServiceList().setAllServices( newServices ); // Services in neuem Spiel speichern
                newGame.setPlayers( newGame.getServiceList().getGame() + "/" + newGame.getName() + "/players" );
            }
            catch ( UnirestException e )
            {
                System.err.println( "Initialisieren der Serviceliste fehlgeschlagen." );
            }
        }
        else
        {
            // ======================== Manuelle Service-Einstellungen ======================== //

            newServices.put( "games", "http://localhost:4567/games" );
            newServices.put( "dice", "http://localhost:4567/dice" );
            newServices.put( "boards", "http://localhost:4567/boards" );
            newServices.put( "bank", "http://localhost:4567/banks" );
            newServices.put( "broker", "http://localhost:4567/broker" );
            newServices.put( "decks", "http://localhost:4567/decks" );
            newServices.put( "events", "http://localhost:4567/events" );
            newServices.put( "users", "http://localhost:4567/users" );

            newGame.getServiceList().setAllServices( newServices );
            newGame.setPlayers( newGame.getServiceList().getGame() + "/" + newGame.getName() + "/players" );
        }
        return newGame;
    }

    /**
     * Legt Komponenten fuer das Spiel an.
     * 
     * @param game
     * @return
     */
    private Game initGameComponents( Game game )
    {
        game = createBoard( game );
        game = createBank( game );
        game = createBroker( game );

        // initialize dice
        game.getComponents().setDice( game.getServiceList().getDice() );

        // initialize events
        game.getComponents().setEvents( game.getServiceList().getEvents() );

        // initialize game
        game.getComponents().setGame( game.getServiceList().getGame() + "/" + game.getName() );

        // initialize deck
        game.getComponents().setDecks( game.getServiceList().getDecks() + "/" + game.getName() );

        return game;
    }

    private Game createBoard( Game game )
    {
        String boardsUrl = game.getServiceList().getBoard();

        CreateBoardDTO createBoardDTO = new CreateBoardDTO();
        createBoardDTO.setGame( game.getId() );

        int responseCode = HttpService.post( boardsUrl, createBoardDTO );

        if ( DEBUG_MODE )
        {
            System.out.println( "\n********************CREATE BOARD FOR " + game.getName() + "********************" );
            System.out.println( "TO: " + boardsUrl );
            System.out.println( "With DTO: " + new Gson().toJson( createBoardDTO ) );
        }

        if ( responseCode == CREATE_BOARD_RESP_CODE )
        {
            game.getComponents().setBoard( boardsUrl + "/" + game.getName() );

            if ( DEBUG_MODE )
                System.out.println( "Board created: " + game.getComponents().getBoard() );
        }
        else
        {
            System.err.println( "Fehler beim erstellen eines Boards." );
        }

        // ***************** Wartezeit zur Reaktion der anderen Systeme ***************** //
        try
        {
            TimeUnit.SECONDS.sleep( 1 );
        }
        catch ( InterruptedException e )
        {
            System.err.println( "Prozess hat in der Wartephase zwischen Erstellen und Initialisieren eines Boards ein Interrupt empfangen." );
        }
        // ****************************************************************************** //

        boardsUrl = ( boardsUrl + "/" + game.getName() );   // Spiele-Namen an Boards-URL anfuegen
        InitBoardDTO initBoardDTO = new InitBoardDTO();
        initBoardDTO.setId( ID_PREFIX_FOR_INIT_BOARDS + game.getName() );

        if ( DEBUG_MODE )
        {
            System.out.println( "\n********************Initiate BOARD FOR " + game.getName() + "********************" );
            System.out.println( "TO: " + boardsUrl );
            System.out.println( "With DTO: " + new Gson().toJson( initBoardDTO ) );
        }

        responseCode = HttpService.put( boardsUrl, initBoardDTO );

        if ( responseCode != INIT_BOARD_RESP_CODE )
        {
            System.err.println( "Fehler beim initialisieren eines Boards" );
        }

        return game;
    }

    private Game createBank( Game game )
    {
        String bankUrl = game.getServiceList().getBank();
        BankDTO bankDTO = new BankDTO();
        bankDTO.setGame( game.getId() );

        int responseCode = HttpService.post( bankUrl, bankDTO );

        if ( DEBUG_MODE )
        {
            System.out.println( "\n********************CREATE BANK ACCOUNT FOR " + game.getName() + "********************" );
            System.out.println( "TO: " + bankUrl );
            System.out.println( "With DTO: " + new Gson().toJson( bankDTO ) );
        }

        if ( responseCode == 200 )
        {
            game.getComponents().setBank( bankUrl + "/" + game.getName() );
        }
        else
        {
            System.err.println( "Bank erstellen fehlgeschlagen." );
        }

        return game;
    }

    private Game createBroker( Game game )
    {
        String brokerUrl = game.getServiceList().getBroker();
        BrokerDTO brokerDTO = new BrokerDTO();
        brokerDTO.setGame( game.getId() );

        int responseCode = HttpService.post( brokerUrl, brokerDTO );

        if ( DEBUG_MODE )
        {
            System.out.println( "\n********************CREATE BROKER FOR " + game.getName() + "********************" );
            System.out.println( "With DTO: " + new Gson().toJson( brokerDTO ) );
        }

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

    /**
     * Initialisiert die GET-Methode zur Abfrage der aktuellen Spiele
     * 
     * URI: /games
     */
    public String getAvailableGames( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 200 );

        String result = "";

        if ( !this.games.isEmpty() )
        {
            Gson gson = new Gson();
            GamesListDTO gameList = new GamesListDTO();
            GameDTO gameDTO;

            for ( Game game : this.games.values() )
            {
                gameDTO = new GameDTO();
                gameDTO.setId( game.getId() );
                gameDTO.setName( game.getName() );
                gameDTO.setPlayers( game.getServiceList().getGame() + "/" + game.getName() + "/players" );
                gameDTO.setServices( game.getServiceList() );
                gameDTO.setComponents( game.getComponents() );

                gameList.getGameList().add( gameDTO );
            }

            result = gson.toJson( gameList );
        }
        else
        {
            resp.status( 204 ); // No Content
        }

        return result;
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage des aktuellen Spielstatus
     * 
     * URI: /games/:gameid
     */
    public String getGame( Request req, Response resp )
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
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der im Spiel verwendeten Services
     * 
     * URI: /games/:gameid/services
     */
    public String getGameServices( Request req, Response resp )
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
    }

    /**
     * Initialisiert die PUT-Methode zum setzen eines Services fuer ein Spiel.
     * 
     * URI: /games/:gameid/services
     */
    public synchronized String putGameService( Request req, Response resp )
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
    }

    /**
     * Initialisiert die PUT-Methode zum setzen von ein oder mehrerer Komponenten fuer ein Spiel.
     * 
     * URI: /games/:gameid/components
     */
    public synchronized String putGameComponent( Request req, Response resp )
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
    }

    /**
     * Initialisiert die GET-Methode zur Abfrage der im Spiel verwendeten Services
     * 
     * URI: /games/:gameid/components
     */
    public String getGameComponents( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 );
        String result = "";
        Game game = getGame( req.params( ":gameId" ) );

        if ( game != null && game.getComponents().hasComponents() )
        {
            resp.status( 200 ); // Created

            result = new Gson().toJson( game.getComponents().getAllComponents() );
        }
        else
        {
            resp.status( 404 ); // Resource could not be found
        }

        return result;
    }

    /**
     * Initialisiert die Post-Methode zum erstellen eines neuen Spielers. Greift auf Userservice zu und Speichert den Spieler dort (kA wozu TODO).
     * 
     * return nur Statuscodes ( 201 falls Spieler erfolgreich erstellt wurde; 409 falls Spieler bereits vorhanden )
     * 
     * URI: /games/:gameId/players
     */
    public synchronized String createNewPlayer( Request req, Response resp )
    {

        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        Game game = getGame( req.params( ":gameId" ) );

        if ( !game.isRunning() )
        {
            String clientUri = "http://" + req.ip() + ":" + req.port();

            Player newPlayer = new Gson().fromJson( req.body(), Player.class );   // Erstellt Playerobjekt mit Namen
            String mapKey = newPlayer.getUserName().toLowerCase();

            // ================= Playerobjekt wird entsprechend der Spezi fuer GameService konfiguriert ================= //

            newPlayer.setId( GAMEID_PREFIX + game.getName() + PLAYERID_INFIX + newPlayer.getUserName().toLowerCase() );
            newPlayer.setUserName( USER_NAME_PREFIX + newPlayer.getUserName().toLowerCase() );

            if ( DEBUG_MODE )
            {
                System.out.println( "\n********************CREATE NEW PLAYER IN GAMESERVICE********************" );
                System.out.println( "NewPlayer: " + new Gson().toJson( newPlayer ) );
            }

            if ( game != null && !game.getPlayers().containsKey( mapKey ) )
            {

                // ================= Playerobjekt wird entsprechend der Spezi fuer UserService konfiguriert ================= //

                CreateUserDTO userServiceDTO = new CreateUserDTO();
                userServiceDTO.setName( newPlayer.getUserName().replaceAll( "/users/", "" ) );
                userServiceDTO.setUri( clientUri + "/client/" + userServiceDTO.getName() + "/events");

                if ( DEBUG_MODE )
                {
                    System.out.println( "\n********************CREATE NEW USER IN USERSERVICE********************" );
                    System.out.println( "NewUser: " + new Gson().toJson( userServiceDTO ) );
                }

                // ================= Post an UserService (User wird im UserService erstellt) ================= //

                HttpService.post( game.getServiceList().getUsers(), userServiceDTO );

                game.getPlayers().put( mapKey, newPlayer );

                createPawn( newPlayer, game );

                createAccount( newPlayer, game );

                game.getPlayers().put( mapKey, newPlayer );

                resp.status( 201 ); // created
            }
            else
            {
                resp.status( 409 ); // Conflict
            }
        }
        else
        {
            resp.status( 409 ); // Conflict
        }

        return "";
    }

    private Player createPawn( Player player, Game game )
    {
        PawnDTO newPawn = new PawnDTO();

        newPawn.setPlayer( player.getId() );
        // newPawn.setPlace( "/boards/" + game.getName() + "/places/" + "0" ); // TODO muss automatisiert werden, aber wie? Wer "putet", gameservice oder boards?
        // newPawn.setPosition( 0 ); // TODO SAME SAME

        if ( DEBUG_MODE )
        {
            System.out.println( "\n********************CREATE NEW PAWN FOR " + player.getUserName() + "********************" );
            System.out.println( "FOR GAME : " + game.getName() );
            System.out.println( "TO : " + game.getComponents().getBoard() + "/pawns" );
            System.out.println( "With DTO : " + new Gson().toJson( newPawn ) );
        }

        int responseCode = HttpService.post( game.getComponents().getBoard() + "/pawns", newPawn );

        if ( responseCode == 200 )
        {
            player.setPawn( game.getComponents().getBoard() + "/pawns" + player.getUserName().replaceAll( "/users", "" ) );

            if ( DEBUG_MODE )
                System.out.println( "New Pawn URI : " + player.getPawn() );
        }
        else
        {
            System.err.println( "Pawn erstellen fehlgeschlagen" );      // TODO Fehlerbehandlung falls Pawn nicht erstellt werden kann (Neuversuch alle 3s?).
        }

        return player;
    }

    private void createAccount( Player player, Game game )
    {

        NewAccountDTO newAccount = new NewAccountDTO();

        newAccount.setPlayer( player.getId() );
        newAccount.setSaldo( START_SALDO );
        String bankURL = game.getComponents().getBank() + "/accounts";

        if ( DEBUG_MODE )
        {
            System.out.println( "\n********************CREATE NEW ACCOUNT FOR " + player.getUserName() + "********************" );
            System.out.println( "FOR GAME : " + game.getName() );
            System.out.println( "TO : " + bankURL );
            System.out.println( "With DTO : " + new Gson().toJson( newAccount ) );
        }

        int responseCode = HttpService.post( bankURL, newAccount );

        if ( responseCode == 201 )
        {
            player.setAccount( bankURL + "/" + player.getUserName().replaceAll( USER_NAME_PREFIX, "" ) );

            if ( DEBUG_MODE )
                System.out.println( "New Account URI : " + player.getAccount() );
        }
        else
        {
            System.err.println( "Account erstellen fehlgeschlagen" );      // TODO Fehlerbehandlung falls Pawn nicht erstellt werden kann (Neuversuch alle 3s?).
        }
    }

    /**
     * Initialisiert die GET-Methode zum Erfragen der Bereitschaft eines Spielers.
     * 
     * URI: /games/:gameId/players
     */
    public String getAllPlayers( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        String result = "";
        Game game = getGame( req.params( ":gameId" ) );

        if ( game != null )
        {
            AllPlayersArrayDTO players = new AllPlayersArrayDTO( game.getPlayers().values().size() );

            AllPlayersDTO playerDTO = new AllPlayersDTO();
            int i = 0;
            for ( Player player : game.getPlayers().values() )
            {
                playerDTO = new AllPlayersDTO();
                playerDTO.setId( player.getId() );
                players.getPlayers()[i] = playerDTO;
                i++;
            }

            result = new Gson().toJson( players );
            resp.status( 200 ); // OK
        }
        else
        {
            resp.status( 400 ); // Bad Request
        }

        return result;
    }

    /**
     * Initialisiert die GET-Methode zum Erfragen der Bereitschaft eines Spielers.
     * 
     * URI: /games/:gameId/players/:playerId/ready
     */
    public String getPlayerReadyness( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        String result = "";
        Game game = getGame( req.params( ":gameId" ) );
        String mapKey = ( req.params( ":playerId" ).toLowerCase() );

        Player player = game.getPlayers().get( mapKey );

        if ( player != null )
        {
            result = new Gson().toJson( player.getReadyness() );
            resp.status( 200 );
        }
        else
        {
            resp.status( 400 ); // Bad Request
        }

        return result;
    }

    /**
     * Initialisiert die PUT-Methode zum Aendern der Bereitschaft eines Spielers.
     * 
     * URI: /games/:gameid/players/:playerid/ready
     */
    public String putPlayerReadyness( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        String mapKey = ( req.params( ":playerId" ).toLowerCase() );
        Game game = getGame( req.params( ":gameId" ) );

        if ( game != null )
        {
            Player player = game.getPlayers().get( mapKey );

            if ( player != null )
            {
                player.setReady( true );
                resp.status( 200 ); // ok

                if ( game.isRunning() )
                {
                    if ( !player.getId().equals( mutexService.getMutexUser( game.getId() ) ) )  // Falls der Spieler momentan nicht den Mutex hat, wird das ready
                                                                                                // nicht akzeptiert = conflict
                    {
                        resp.status( 409 ); // conflict
                    }
                    else
                    {
                        initNextPlayersTurn( game );      // Erlaubnis fuer das Inanspruchnehmen des Mutex an naechsten Spieler weiter geben
                    }
                    player.setReady( false );
                }
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
    }

    /**
     * Erteilt die Erlaubnis an den naechsten Spieler den Mutex in Anspruchzunehmen
     */
    private void initNextPlayersTurn( Game game )
    {
        // Mitspieler in Array speichern
        Player[] playerArray = new Player[game.getPlayers().size()];
        String currentPlayerID = mutexService.getMutexUser( game.getId() );

        int i = 0;
        int nextPlayerIndex = 0;
        for ( Player player : game.getPlayers().values() )
        {
            playerArray[i] = player;

            if ( player.getId().equals( currentPlayerID ) && i < ( playerArray.length - 1 ) )
                nextPlayerIndex = ( i + 1 );

            i++;
        }

        if ( DEBUG_MODE )
            System.out.println( "\nNext Player: " + new Gson().toJson( playerArray[nextPlayerIndex].getUserName() ) );

        mutexService.release( game.getId(), currentPlayerID );
        mutexService.assignMutexPermission( game.getId(), playerArray[nextPlayerIndex].getId() );
    }

    /**
     * URI: /games/:gameId/players/:playerId
     * 
     * @param req
     * @param resp
     * @return
     */
    public String getSpecificPlayer( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        String result = "";
        Game game = getGame( req.params( ":gameId" ) );
        String playerMapKey = ( req.params( ":playerId" ).toLowerCase() );

        Player player = game.getPlayers().get( playerMapKey );

        if ( player != null )
        {
            resp.status( 200 ); // created

            SpecificPlayerDTO specificPlayerDTO = new SpecificPlayerDTO();

            specificPlayerDTO.setUserName( player.getUserName() );
            specificPlayerDTO.setAccount( player.getAccount() );
            specificPlayerDTO.setId( player.getId() );
            specificPlayerDTO.setPawn( player.getPawn() );
            specificPlayerDTO.setReady( game.getComponents().getGame() + "/players/" + player.getUserName().replaceAll( "/users/", "" ) + "/ready" );

            result = new Gson().toJson( specificPlayerDTO );
        }
        else
        {
            resp.status( 400 ); // Bad Request
        }

        return result;
    }

    /**
     * URI: /games/:gameId/status
     * 
     * @param req
     * @param resp
     * @return
     */
    public String getGameStatus( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        String result = "";
        Game game = getGame( req.params( ":gameId" ) );

        if ( game != null )
        {
            resp.status( 200 ); // created

            result = new Gson().toJson( game.getStatus() );
        }
        else
        {
            resp.status( 400 ); // Bad Request
        }

        return result;
    }

    /**
     * Veraendert bei Aufruf, entsprechend der vorliegenden Daten, den Status des Spiels. Standardwert: "registration", nach Erstellen des Spiels. Das Aufrufen der put-Methode
     * bewirkt erst eine Veraenderung, wenn der Status aller Spieler auf "ready" geaendert wurde, dann aendert sich bei Aufruf der Status auf "running". Sobald alle Kriterien
     * erfuellt sind, die das Spiel laut Regeln beenden, bewirkt das erneute Aufrufen der put-Methode eine Aenderung des Statuses in "finished".
     * 
     * return nur Statuscodes ( 200: Status geaendert; 409: Status konnte nicht geaendert werden )
     * 
     * URI: /games/:gameId/status
     */
    public String putGameStatus( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        Game game = getGame( req.params( ":gameId" ) );

        if ( game != null && !game.getPlayers().isEmpty() )
        {
            if ( game.allPlayersReady() && !game.isRunning() )
            {
                startGame( game );
                resp.status( 200 ); // ok
            }
            // else if ( game.isFinshed() )
            // {
            //
            // }
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
    }

    /**
     * Versetzt das Spiel in den Status running und waehlt den ersten Spieler aus.
     * 
     * @param game
     */
    private void startGame( Game game )
    {
        game.setStatus( "running" );

        // ready alles Spieler wieder auf false setzen
        for ( Player player : game.getPlayers().values() )
            player.setReady( false );

        // Mitspieler in Array speichern
        Player[] playerArray = new Player[game.getPlayers().size()];

        int i = 0;
        for ( Player player : game.getPlayers().values() )
        {
            playerArray[i] = player;
            i++;
        }

        // ersten Spieler auswählen
        int firstPlayer = (int) ( Math.random() * playerArray.length );

        // Erlaubnis an Spieler uebergeben den Mutex zu belegen
        mutexService.assignMutexPermission( game.getId(), playerArray[firstPlayer].getId() );

        mutexService.acquire( game.getId(), playerArray[firstPlayer].getId() );

        if ( DEBUG_MODE )
            System.out.println( "\nErster Spieler: " + playerArray[firstPlayer].getId() );
    }

    /**
     * Versucht den Mutex fuer den jeweiligen Spieler zuerhalten. Klappt nur wenn dieser auch an der Reihe ist.
     * 
     * Uebergeben werden muss ein Playerobjekt als json. ( Die Methode verwendet nur die ID des Playerobjekts, die anderen Felder koennen leer bleiben )
     * 
     * URI: /games/:gameId/player/turn
     */
    public String putPlayersTurn( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        Game game = getGame( req.params( ":gameId" ) );
        Player player = new Gson().fromJson( req.body(), Player.class );

        if ( game != null && game.isRunning() && player != null )       // TODO Fehlercode falls der Spieler den Mutex bereits hat
        {
            if ( mutexService.acquire( game.getId(), player.getId() ) )
            {
                resp.status( 201 ); // aquired the mutex

                if ( DEBUG_MODE )
                    System.out.println( "\nMutex acquired to: " + player.getId() );
            }
            else
                resp.status( 409 ); // already aquired by an other player
        }
        else
        {
            resp.status( 400 ); // Bad Request
        }
        return "";
    }

    /**
     * URI: /games/:gameId/player/turn
     * 
     * @param req
     * @param resp
     * @return
     */
    public String getPlayersTurn( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        String result = "";
        Game game = getGame( req.params( ":gameId" ) );

        if ( game != null && game.isRunning() && !mutexService.getMutexUser( game.getId() ).equals( "" ) )
        {
            resp.status( 200 ); // ok

            String currentPlayerID = mutexService.getMutexUser( game.getId() );
            String currentPlayerName = currentPlayerID.replaceAll( "/games/" + game.getName() + "/players/", "" );      // TODO
            Player currentPlayer = game.getPlayers().get( currentPlayerName );
            CurrentPlayerDTO currentPlayerDTO = new CurrentPlayerDTO();

            currentPlayerDTO.setId( currentPlayer.getId() );
            currentPlayerDTO.setUser( currentPlayer.getUserName() ); // TODO soll die Uri sein
            currentPlayerDTO.setPawn( currentPlayer.getPawn() );
            currentPlayerDTO.setAccount( currentPlayer.getAccount() );
            currentPlayerDTO.setReady( game.getComponents().getGame() + "/players/" + currentPlayerDTO.getUser().replaceAll( "/user/", "" ) + "/ready" );

            result = new Gson().toJson( currentPlayerDTO );
        }
        else
        {
            resp.status( 400 ); // Bad Request
        }

        return result;
    }

    /**
     * URI: /games/:gameId/player/current
     * 
     * @param req
     * @param resp
     * @return
     */
    public String getPlayersCurrent( Request req, Response resp )
    {
        resp.header( "Content-Type", "application/json" );
        resp.status( 500 ); // Internal Server Error

        String result = "";
        Game game = getGame( req.params( ":gameId" ) );

        if ( game != null && game.isRunning() && !mutexService.getMutexPermittedUser( game.getId() ).equals( "" ) )
        {
            resp.status( 200 ); // ok

            String currentPlayerID = mutexService.getMutexPermittedUser( game.getId() );
            String currentPlayerName = currentPlayerID.replaceAll( "/games/" + game.getName() + "/players/", "" );      // TODO
            Player currentPlayer = game.getPlayers().get( currentPlayerName );
            CurrentPlayerDTO currentPlayerDTO = new CurrentPlayerDTO();

            currentPlayerDTO.setId( currentPlayer.getId() );
            currentPlayerDTO.setUser( currentPlayer.getUserName() ); // TODO soll die Uri sein
            currentPlayerDTO.setPawn( currentPlayer.getPawn() );
            currentPlayerDTO.setAccount( currentPlayer.getAccount() );
            currentPlayerDTO.setReady( game.getComponents().getGame() + "/players/" + currentPlayerDTO.getUser().replaceAll( "/user/", "" ) + "/ready" );

            result = new Gson().toJson( currentPlayerDTO );
        }
        else
        {
            resp.status( 400 ); // Bad Request
        }

        return result;
    }

    private Game getGame( String gameId )
    {
        String gameMapKey = GAMEID_PREFIX + gameId;
        gameMapKey.toLowerCase();

        return games.get( gameMapKey );
    }

}
