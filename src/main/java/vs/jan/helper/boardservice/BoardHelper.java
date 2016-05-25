package vs.jan.helper.boardservice;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.json.JSONGameURI;
import vs.jan.json.JSONThrowsList;
import vs.jan.json.JSONThrowsURI;
import vs.jan.model.Board;
import vs.jan.model.Field;
import vs.jan.model.Pawn;
import vs.jan.model.Place;
import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jan.model.User;
import vs.jan.model.exception.Error;
import vs.jan.tools.HttpService;
import vs.jonas.services.json.EventData;
import vs.jonas.services.model.Dice;
import vs.jonas.services.model.Event;

public class BoardHelper {

	private final Gson GSON = new Gson();
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
	 */
	public void postEvent(String gameid, String type, String name, Pawn pawn, Map<String, Service> neededServices) {

		Service service = neededServices.get(ServiceNames.EVENT);
		String reas = null;
		String resource = null;

		switch (type) {
		case "move": {
			reas = pawn.getPlayerUri() + " has moved the pawn: " + pawn.getPawnUri() + " to: " + pawn.getPlaceUri();
			resource = pawn.getRollsUri();
			break;
		}
		}

		EventData event = new EventData(gameid, type, name, reas, resource, pawn.getPlayerUri());
		HttpService.post(service.getUri(), event, HttpURLConnection.HTTP_OK);
	}

	/**
	 * Holt alle Events des Spielers seit dem letzten Wurf
	 * 
	 * @param pawn
	 *          Die Figur
	 * @param gameid
	 *          Das Board
	 * @param date
	 *          Der aktuelle Zeitpunkt des Wurfes
	 * @return List<Event> Die Liste aller Events fuer diesen Wurf
	 * 
	 */
	public List<Event> retrieveEventList(Pawn pawn, String gameid, Date date) {
		
		return new ArrayList<>();
	}

	/**
	 * Liefert das Board zu einer Gameid, muss spaeter erweitert werden, falls
	 * mehere Boards einem Spiel zugeteilt werden koennen.
	 * 
	 * @param boards2
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @return Board Das Board zu der Gameid
	 */
	public Board getBoard(Map<Board, JSONGameURI> boards, String gameid) {

		for (Board b : boards.keySet()) {
			if (boards.get(b).getURI().contains(gameid)) {
				return b;
			}
		}
		throw new ResourceNotFoundException(Error.BOARD_NOT_FOUND.getMsg());
	}

	/**
	 * TODO: implementiere URI-Erzeugung Generiert die Pawn-Uri aus dem
	 * GET-Response der PlayerUri
	 * 
	 * @param board
	 *          The Board
	 * @param playerUri
	 *          The PlayerUri
	 * @return Pawn-Uri
	 */
	public String getPawnUri(Board board, String playerUri) {

		String[] uri = playerUri.split("/");

		String id = uri[uri.length - 1];
		int num = 0;
		for (Field f : board.getFields()) {
			while (hasSamePawnID(f, id)) {
				num++;
				id = uri[uri.length - 1] + "_" + num;
			}
		}
		return board.getUri() + "/pawns/" + id;
	}

	private boolean hasSamePawnID(Field f, String pawnUri) {

		for (Pawn p : f.getPawns()) {
			if (p.getPawnUri().contains(pawnUri)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gibt die Figur, die zu der Pawnid gehoert zurueck
	 * 
	 * @param board
	 *          Das Board auf dem die Figur steht
	 * @param pawnid
	 *          Die ID der Figur
	 * @return Die angeforderte Figur
	 */
	public Pawn getPawn(Board board, String pawnid) {

		for (Field f : board.getFields()) {
			for (Pawn p : f.getPawns()) {
				if (p.getPawnUri().contains(pawnid)) {
					return p;
				}
			}
		}
		throw new ResourceNotFoundException(Error.PAWN_NOT_FOUND.getMsg());
	}

	public Place getPlace(List<Field> fields, String placeid) {

		for (Field f : fields) {
			Place p = f.getPlace();
			String[] uri = p.getPlaceUri().split("/");
			String id = uri[uri.length - 1];
			if (id.equals(placeid)) {
				return p;
			}
		}
		throw new ResourceNotFoundException(Error.PLACE_NOT_FOUND.getMsg());
	}


	public Field getField(Board key, String placeUri) {

		for (Field f : key.getFields()) {
			Place p = f.getPlace();
			if (p != null && p.getPlaceUri().equals(placeUri)) {
				return f;
			}
		}
		throw new ResourceNotFoundException(Error.PLACE_NOT_FOUND.getMsg());
	}

	public String getID(String uri) {
		String [] u = uri.split("/");
		return u[u.length - 1];
	}

	public void addThrow(Map<JSONThrowsURI, JSONThrowsList> throwMap, Pawn pawn, Dice roll) {
		
		for(JSONThrowsURI uri: throwMap.keySet()){
			if(uri.getRollUri().contains(getID(pawn.getPawnUri()))){
				throwMap.get(uri).addThrow(roll);
			}
		}
	}
	
	/**
	 * Ermittelt den Spieler zu der Figur vom Game-Service
	 * 
	 * @param pawn
	 *          Die Figur des Spielers
	 * @param gameid
	 *          Die Gameid zum Spiel
	 * @return User Der Spieler der wuerfeln moechte
	 * @throws ResourceNotFoundException
	 *           Spieler wurde nicht gefunden
	 * @throws ConnectionRefusedException
	 *           Service nicht erreichbar
	 */
	public User getPlayer(Pawn pawn, String gameid) {
		String json = HttpService.get(pawn.getPlayerUri(), HttpURLConnection.HTTP_OK);
		User currPlayer = GSON.fromJson(json, User.class);
		return currPlayer;
	}
	
}
