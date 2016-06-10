package vs.jan.api.decksservice;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import java.net.HttpURLConnection;

import com.google.gson.Gson;

import vs.jan.exception.NotImplementedException;
import vs.jan.model.exception.Error;
import vs.jan.services.DecksService;

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
			throw new NotImplementedException(Error.NOT_IMPL.getMsg());
		});
		
	}

	private void initGetChance() {
		get(" /decks/:gameid/chance ", CONTENT_TYPE, (req, resp) -> {
			throw new NotImplementedException(Error.NOT_IMPL.getMsg());
		});
	}

	private void initGetDecks() {
		get(" /decks", CONTENT_TYPE, (req, resp) -> {
			throw new NotImplementedException(Error.NOT_IMPL.getMsg());
		});
	}
	
	private void initPostDecks() {
		post("/decks", CONTENT_TYPE, (req, resp) -> {
			throw new NotImplementedException(Error.NOT_IMPL.getMsg());
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
