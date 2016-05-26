import com.google.gson.Gson;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.jan.json.boardservice.JSONService;
import vs.jan.model.ServiceNames;
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

    private final int TIMEOUT = 1000;
    private BoardRESTApi boardApi;
    private Gson gson;

    private String boardUri;
    private String gamesUri;

    /**
     * Run Main
     */
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

    /**
     * Starts All Services
     * @throws ServiceUnavailableException
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    public void startServices() throws ServiceUnavailableException, MalformedURLException, InterruptedException {

        gson = new Gson();

        /*  Initializes BankService  */
//        BankService.run();

        /*  Initializes EventService */
        new EventService().startService();

        /* Initializes YellowPages - get local services */
        YellowPagesService yellowPages = new YellowPagesService(false);

        /*  Initializes DiceService (needs a running EventService)   */
        Map<String, JSONService> neededServicesDice = new HashMap<>();
        neededServicesDice.put(ServiceNames.EVENT, yellowPages.getService(ServiceNames.EVENT));
        new DiceService(neededServicesDice).startService();

        /*  Initializes GameService */
        new GamesService();

        /*  Initializes UserService */
        new UserServiceRESTApi();

        /*  Initializes BoardService */
        boardApi = new BoardRESTApi();


        // Initialize URIs
        gamesUri = yellowPages.getService(ServiceNames.GAME).getUri();//"http://localhost:4567/games";
        boardUri = yellowPages.getService(ServiceNames.BOARD).getUri();

//        setupUser(boardID, gamesUri);
//        setupBoard();

        int boardID = 100;
        CreateGameExDTO g = new CreateGameExDTO();
        g.setName("" + boardID);
        HttpService.post(gamesUri, g, HttpURLConnection.HTTP_CREATED);
        System.out.println(HttpService.get(gamesUri, 200));

        setupUser(boardID,gamesUri);
        System.out.println(HttpService.get(gamesUri, 200));

    }

    /**
     * Creates the players
     * @param boardID
     * @param gameUri
     * @throws InterruptedException
     */
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
        HttpService.post(gamePlayerUri, p1, HttpURLConnection.HTTP_CREATED);
        HttpService.post(gamePlayerUri, p2, HttpURLConnection.HTTP_CREATED);
        HttpService.post(gamePlayerUri, p3, HttpURLConnection.HTTP_CREATED);
        HttpService.post(gamePlayerUri, p4, HttpURLConnection.HTTP_CREATED);
        System.out.println("SUCCESS");
    }
}
