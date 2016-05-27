package vs.jan.helper;

import java.net.HttpURLConnection;
import com.google.gson.Gson;

import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.ResponseCodeException;
import vs.jan.model.User;
import vs.jan.model.boardservice.Pawn;
import vs.jan.tools.HttpService;

public abstract class Helper {
	
	protected final Gson GSON = new Gson();

	public String getID(String uri) {
		String [] u = uri.split("/");
		return u[u.length - 1];
	}
	
	/**
	 * Ermittelt den Spieler zu der Figur vom Game-Service
	 * 
	 * @param pawn
	 *          Die Figur des Spielers
	 * @param gameid
	 *          Die Gameid zum Spiel
	 * @return User Der Spieler der wuerfeln moechte
	 * @throws ResponseCodeException 
	 * @throws ResourceNotFoundException
	 *           Spieler wurde nicht gefunden
	 * @throws ConnectionRefusedException
	 *           Service nicht erreichbar
	 */
	public User getPlayer(Pawn pawn, String gameid) 
			throws ResponseCodeException {
		String json = HttpService.get(pawn.getPlayerUri(), HttpURLConnection.HTTP_OK);
		User currPlayer = GSON.fromJson(json, User.class);
		return currPlayer;
	}
}
