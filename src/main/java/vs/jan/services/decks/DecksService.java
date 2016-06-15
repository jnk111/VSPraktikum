package vs.jan.services.decks;

import java.util.HashMap;
import java.util.Map;

import vs.jan.api.decksservice.JSONDecksList;
import vs.jan.helper.decksservice.DecksHelper;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.json.decksservice.JSONCard;
import vs.jan.model.decksservice.ChanceCard;
import vs.jan.model.decksservice.CommCard;
import vs.jan.model.decksservice.Decks;
import vs.jan.model.exception.Error;
import vs.jan.validator.DecksValidator;

public class DecksService {

	private final String DECKS_INFIX = "/decks/";
	private final String COMMUNITY_SUFFIX = "/community";
	private final String CHANCE_SUFFIX = "/chance";
	
	private Map<String, Decks> decks;
	private DecksValidator validator;
	private DecksHelper helper;

	public DecksService() {
		this.decks = new HashMap<>();
		this.validator = new DecksValidator();
		this.helper = new DecksHelper();
	}

	public JSONDecksList getAllDecksURIS() {

		JSONDecksList list = new JSONDecksList();
		this.decks.forEach((k, v) -> list.getDecks().add(v.getUri()));
		return list;

	}

	public void createDecks(JSONGameURI uri) {
		validator.checkJsonIsValid(uri, Error.JSON_GAME_URI.getMsg());
		String id = DecksHelper.getID(uri.getURI());
		String decksUri = DECKS_INFIX + id;
		String commUri = decksUri + COMMUNITY_SUFFIX;
		String chanceUri = decksUri + CHANCE_SUFFIX;
		Decks decks = new Decks(decksUri, commUri, chanceUri);
		this.decks.put(id, decks);		
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
