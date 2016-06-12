package vs.jan.api.decksservice;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import java.net.HttpURLConnection;

import com.google.gson.Gson;

import vs.jan.exception.NotImplementedException;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.model.StatusCodes;
import vs.jan.model.exception.Error;
import vs.jan.services.decks.DecksService;
import vs.jan.services.decks.JSONCard;

public class DecksAPI {
	
	private final String CLRF = "\r" + "\n"; // Newline
	private final String CONTENT_TYPE = "application/json";
	private final Gson GSON = new Gson();
	private DecksService service = new DecksService();
	
	public DecksAPI(){
		initGet();
		initPost();
		initExceptions();
	}

	private void initPost() {
		initPostDecks();		
	}

	private void initGet() {
		initGetDecks();
		initGetChance();
		initGetCommunity();
	}

	private void initGetCommunity() {
		get(" /decks/:gameid/community", CONTENT_TYPE, (req, resp) -> {
			JSONCard comm = service.getNextCommunityCard(req.params(":gameid"));
			return GSON.toJson(comm);
		});
		
	}

	private void initGetChance() {
		get(" /decks/:gameid/chance ", CONTENT_TYPE, (req, resp) -> {
			JSONCard chance = service.getNextChanceCard(req.params(":gameid"));
			return GSON.toJson(chance);
		});
	}

	private void initGetDecks() {
		get(" /decks", CONTENT_TYPE, (req, resp) -> {
			JSONDecksList list = service.getAllDecksURIS();
			return GSON.toJson(list);
		});
	}
	
	private void initPostDecks() {
		post("/decks", CONTENT_TYPE, (req, resp) -> {
			JSONGameURI uri = GSON.fromJson(req.body(), JSONGameURI.class);
			service.createDecks(uri);
			return StatusCodes.CREATED + CLRF;
		});
	}
	
	private void initExceptions() {
		
		exception(NotImplementedException.class, (exception, request, response) -> {
			response.status(HttpURLConnection.HTTP_BAD_METHOD);
			response.body(HttpURLConnection.HTTP_BAD_METHOD + ": " + exception.getMessage());
			exception.printStackTrace();
		});
		
	}
	
	

}
