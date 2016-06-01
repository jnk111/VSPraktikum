package vs.jan.helper;

import java.net.HttpURLConnection;
import java.util.Date;

import com.google.gson.Gson;

import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.ResponseCodeException;
import vs.jan.json.boardservice.JSONEvent;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.model.ServiceList;
import vs.jan.model.boardservice.Player;
import vs.jan.tools.HttpService;

public abstract class Helper {
	
	protected final Gson GSON = new Gson();
	protected ServiceList services;

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
		System.out.println("PlayerUri: " + playerUri);
		String json = HttpService.get(playerUri, HttpURLConnection.HTTP_OK);
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
	public void postEvent(JSONEvent event, String uri) throws ResponseCodeException {

		HttpService.post(uri, event, HttpURLConnection.HTTP_OK);
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
	public JSONEventList retrieveEventList(String eventServiceUri, String playeruri, String gameid, Date date) {
		//
		String url = eventServiceUri + "?game=" + gameid + "&player=" + playeruri;
		String json = HttpService.get(url, HttpURLConnection.HTTP_OK);
		JSONEventList list = GSON.fromJson(json, JSONEventList.class);
		return list;
	}
	
	public ServiceList getServices() {
		return services;
	}
	public void setServices(ServiceList services) {
		this.services = services;
	}
}
