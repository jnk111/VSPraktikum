package vs.jan.helper;

import java.net.HttpURLConnection;
import com.google.gson.Gson;

import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.ResponseCodeException;
import vs.jan.model.User;
import vs.jan.model.boardservice.Pawn;
import vs.jan.model.boardservice.Player;
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
	 * @param playeruri
	 *          Die Uri des Spielers
	 * @param gameid
	 *          Die Gameid zum Spiel
	 * @return User Der Spieler der wuerfeln moechte
	 * @throws ResponseCodeException 
	 * @throws ResourceNotFoundException
	 *           Spieler wurde nicht gefunden
	 * @throws ConnectionRefusedException
	 *           Service nicht erreichbar
	 */
	public Player getPlayer(String playerUri, String gameid) 
			throws ResponseCodeException {
		String json = HttpService.get(playerUri, HttpURLConnection.HTTP_OK);
		Player currPlayer = GSON.fromJson(json, Player.class);
		return currPlayer;
	}
}
