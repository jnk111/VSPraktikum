import com.google.gson.Gson;
import vs.gerriet.service.BankService;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.jan.helper.boardservice.BoardServiceHelper;
import vs.jan.model.*;
import vs.jan.tools.HttpService;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;
import vs.jonas.services.services.YellowPagesService;
import vs.malte.example.json.CreateGameExDTO;
import vs.malte.models.Player;
import vs.malte.services.GamesService;

import javax.naming.ServiceUnavailableException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jones on 24.05.2016.
 */
public class StartLocalServices {

    BoardRESTApi boardApi;
    private Gson gson;
    private static final int TIMEOUT = 1000;


    public static void main(String[] args) {
        try {
            new StartLocalServices().startServices();
        } catch (ServiceUnavailableException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startServices() throws ServiceUnavailableException, MalformedURLException, InterruptedException {

        gson = new Gson();

        /*  Initializes BankService  */
//        BankService.run();

        /*  Initializes EventService */
        new EventService().startService();

        /* Initializes YellowPages - get local services */
        YellowPagesService yellowPages = new YellowPagesService(YellowPagesService.LOCAL_SERVICES);

        /*  Initializes DiceService (needs a running EventService)   */
        Map<String, Service> neededServicesDice = new HashMap<>();
        neededServicesDice.put(ServiceNames.EVENT, yellowPages.getService(ServiceNames.EVENT));
        new DiceService(neededServicesDice).startService();

        /*  Initializes UserService */
        new UserServiceRESTApi();

        /*  Initializes BoardService */
        boardApi = new BoardRESTApi();

        /*  Initializes GameService */
        new GamesService();
        setupGame();

    }

    private void setupGame() throws InterruptedException, MalformedURLException {
        setupBoard();

    }

    private void setupBoard() throws InterruptedException, MalformedURLException {
        String gameUri = "http://localhost:4567/games";
        int boardID = 42;

        createBoard(boardID, gameUri);
        placeBoard(boardID);
        setupUser(boardID, gameUri);
    }

    private void createBoard(int boardID, String gameUri) throws InterruptedException {

        Thread.sleep(TIMEOUT);
        System.out.println("Create Game on: " + gameUri);
        CreateGameExDTO g = new CreateGameExDTO();
        g.setName("" + boardID);
        HttpService.post(gameUri, g, HttpURLConnection.HTTP_CREATED);
    }

    private void placeBoard(int boardID) throws InterruptedException {

        System.out.println("Place the Board");

        Board b = new BoardServiceHelper().getBoard(boardApi.getBoardService().getBoards(), "" + boardID);
        b.setPlayers("http://localhost:4567/game/" + boardID + "/players");
        for (int i = 0; i <= 10; i++) {
            Field f = new Field();
            Place p = new Place("/boards/42/places/" + i);
            f.setPlace(p);
            b.getFields().add(f);
        }

        Thread.sleep(TIMEOUT);

        System.out.println("Fill Board with Information...");
        HttpService.put("http://localhost:4567/boards/" + boardID, b.convert(), HttpURLConnection.HTTP_OK);
        System.out.println("SUCCESS");
        System.out.println("Placed Board: " + gson.toJson(b.convert()));
    }

    private void setupUser(int boardID, String gameUri) throws InterruptedException {

        Thread.sleep(TIMEOUT);
        Player p1 = new Player();
        Player p2 = new Player();
        Player p3 = new Player();
        Player p4 = new Player();
        p1.setUserName("mario");
        p2.setUserName("wario");
        p3.setUserName("yoshi");
        p4.setUserName("donkeykong");
        String gamePlayerUri = gameUri + "/" + boardID + "/players";
        System.out.println("Create Some Users on Game: " + boardID);
        createUser(boardID, gamePlayerUri, p1);
        createUser(boardID, gamePlayerUri, p2);
        createUser(boardID, gamePlayerUri, p3);
        createUser(boardID, gamePlayerUri, p4);
        System.out.println("SUCCESS");
    }

    private void createUser(int boardID, String gamePlayerUri, Player player) throws InterruptedException {
        // Thread.sleep(TIMEOUT);
        HttpService.post(gamePlayerUri, player, HttpURLConnection.HTTP_CREATED);
        System.out.println("Created Player: " + gson.toJson(player));
    }
}
