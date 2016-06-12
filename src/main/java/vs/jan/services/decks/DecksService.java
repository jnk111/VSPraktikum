package vs.jan.services.decks;

import java.util.HashMap;
import java.util.Map;

import vs.jan.api.decksservice.JSONDecksList;
import vs.jan.helper.decksservice.DecksHelper;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.model.exception.Error;
import vs.jan.validator.DecksValidator;

public class DecksService {

	private Map<Decks, JSONGameURI> decks;
	private DecksValidator validator;
	private DecksHelper helper;

	public DecksService() {
		this.decks = new HashMap<>();
		this.validator = new DecksValidator();
		this.helper = new DecksHelper();
	}

	public JSONDecksList getAllDecksURIS() {

		JSONDecksList list = new JSONDecksList();

		for (Decks d : this.decks.keySet()) {
			list.addUri(d.getUri());
		}

		return list;

	}

	public void createDecks(JSONGameURI uri) {
		validator.checkJsonIsValid(uri, Error.JSON_GAME_URI.getMsg());
		String decksUri = "/decks/" + helper.getID(uri.getURI());
		String commUri = decksUri + "/community";
		String chanceUri = decksUri + "/chance";
		Decks decks = new Decks(decksUri, commUri, chanceUri);
		this.decks.put(decks, uri);		
	}

	public JSONCard getNextCommunityCard(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		Decks decks = helper.getDecks(this.decks, gameid);
		CommCard next = decks.getNextCommCard();		
		return next.convert();
	}

	public JSONCard getNextChanceCard(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		Decks decks = helper.getDecks(this.decks, gameid);
		ChanceCard next = decks.getNextChanceCard();
		return next.convert();
	}

}
