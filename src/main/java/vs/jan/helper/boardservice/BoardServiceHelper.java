package vs.jan.helper.boardservice;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import vs.jan.exception.ExcMessageHandler;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.json.JSONGameURI;
import vs.jan.json.JSONPlace;
import vs.jan.model.Board;
import vs.jan.model.Field;
import vs.jan.model.Pawn;
import vs.jan.model.PlaceBkp;
import vs.jan.model.Place;
import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jan.tools.HttpService;
import vs.jonas.services.json.EventData;
import vs.jonas.services.model.Event;

public class BoardServiceHelper {

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
		return null;
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
		return null;
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
		return null;
	}

	public Place getPlaceWithPathInfo(JSONPlace place, List<Field> fields, String pathinfo) {

		for (Field f : fields) {
			Place p = f.getPlace();
			if (p.getPlaceUri().equals(pathinfo)) {
				p.setName(place.getName());
				p.setBrokerUri(place.getBroker());
				return p;
			}
		}
		return null;
	}

	// public void initBoardInformation(Board b) {
	//
	// for (Place p : Place.values()) {
	// b.addField(new Field(p));
	// }
	//
	// }

	public Field getField(Board key, String placeUri) {

		for (Field f : key.getFields()) {
			Place p = f.getPlace();
			if (p != null && p.getPlaceUri().equals(placeUri)) {
				return f;
			}
		}
		return null;
	}

	public String getID(String uri) {
		String [] u = uri.split("/");
		return u[u.length - 1];
	}
}
