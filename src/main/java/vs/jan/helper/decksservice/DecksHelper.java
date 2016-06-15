package vs.jan.helper.decksservice;

import java.util.Map;

import vs.jan.exception.ResourceNotFoundException;
import vs.jan.helper.Helper;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.model.decksservice.Decks;
import vs.jan.model.exception.Error;

public class DecksHelper extends Helper {

	public Decks getDecks(Map<String, Decks> decks, String gameid) {

		Decks d = decks.get(gameid);

		if (d != null) {
			return d;
		}

		throw new ResourceNotFoundException(Error.DECKS_NOT_FOUND.getMsg());
	}
}
