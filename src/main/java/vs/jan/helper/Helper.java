package vs.jan.helper;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.ResponseCodeException;
import vs.jan.json.boardservice.JSONEvent;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.json.brokerservice.JSONAccount;
import vs.jan.model.ServiceList;
import vs.jan.model.User;
import vs.jan.model.boardservice.Player;
import vs.jan.tools.HttpService;

public abstract class Helper {

	protected final static Gson GSON = new Gson();
	protected static ServiceList services;

	public static String getID(String uri) {
		String[] u = uri.split("/");
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
	public static Player getPlayer(String playerUri, String gameid) throws ResponseCodeException {
		String json = HttpService.get(services.getGamesHost() + playerUri, HttpURLConnection.HTTP_OK);
		Player currPlayer = GSON.fromJson(json, Player.class);
		return currPlayer;
	}

	/**
	 * Postet ein Event, das fuer eine Figur ausgeloest wurde z. B.: Figur nach
	 * einem Wuerfelwurd zu einer neuen Position bewegen
	 * 
	 * @param gameid
	 *          Die ID des Spiels
	 * @param type
	 *          Der Eventtyp, z. B.: 'move'
	 * @param name
	 *          Der Eventname
	 * @param pawn
	 *          Die Figur um die es sich handelt
	 * @param neededServices2
	 * @throws ResponseCodeException
	 */
	public static void postEvent(JSONEvent event) throws ResponseCodeException {

		if (event != null) {
			HttpService.post(services.getEvents(), event, HttpURLConnection.HTTP_OK);
		}

	}

	/**
	 * Holt alle Events des Spielers seit dem letzten Wurf
	 * 
	 * @param eventServiceUri
	 * 
	 * @param playeruri
	 *          Die Figur
	 * @param gameid
	 *          Das Board
	 * @param date
	 *          Der aktuelle Zeitpunkt des Wurfes
	 * @return List<Event> Die Liste aller Events fuer diesen Wurf
	 * 
	 */
	public static JSONEventList receiveEventList(String playeruri, String gameid, Date date) {
		//
		String url = services.getEvents() + "?game=" + gameid + "&player=" + playeruri;
		String json = HttpService.get(url, HttpURLConnection.HTTP_OK);
		JSONEventList list = GSON.fromJson(json, JSONEventList.class);
		return list;
	}

	public static JSONAccount getAccount(String accountUri) {
		String json = HttpService.get(accountUri, HttpURLConnection.HTTP_OK);
		return GSON.fromJson(json, JSONAccount.class);
	}

	public static User getUser(String uri) {

		String json = HttpService.get(uri, HttpURLConnection.HTTP_OK);
		return GSON.fromJson(json, User.class);
	}

	public static void broadCastEvent(JSONEvent event) {

//		if (event != null) {
//
//			String json = HttpService.get(services.getUsers(), HttpURLConnection.HTTP_OK);
//
//			@SuppressWarnings("unchecked")
//			List<String> uris = GSON.fromJson(json, List.class);
//
//			for (String uri : uris) {
//				String json2 = HttpService.get(services.getUsersHost() + uri, HttpURLConnection.HTTP_OK);
//				User user = GSON.fromJson(json2, User.class);
//				HttpService.post(user.getUri() + "/events", event, HttpURLConnection.HTTP_OK);
//			}
//		}
	}

	public static ServiceList getServices() {
		return services;
	}

	public static void setServices(ServiceList services) {
		Helper.services = services;
	}

	public static Gson getGson() {
		return GSON;
	}
	
	
}
