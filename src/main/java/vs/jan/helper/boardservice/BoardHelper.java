package vs.jan.helper.boardservice;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.Helper;
import vs.jan.json.boardservice.JSONEvent;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.json.boardservice.JSONThrowsList;
import vs.jan.json.boardservice.JSONThrowsURI;
import vs.jan.model.boardservice.Board;
import vs.jan.model.boardservice.Field;
import vs.jan.model.boardservice.Pawn;
import vs.jan.model.boardservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.tools.HttpService;
import vs.jonas.services.model.Dice;

public class BoardHelper extends Helper {

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
	 * @param pawn
	 *          Die Figur
	 * @param gameid
	 *          Das Board
	 * @param date
	 *          Der aktuelle Zeitpunkt des Wurfes
	 * @return List<Event> Die Liste aller Events fuer diesen Wurf
	 * 
	 */
	public JSONEventList retrieveEventList(String eventServiceUri, Pawn pawn, String gameid, Date date) {
		//
		String url = eventServiceUri + "?game=" + gameid + "&player=" + pawn.getPlayerUri();
		String json = HttpService.get(url, HttpURLConnection.HTTP_OK);
		JSONEventList list = GSON.fromJson(json, JSONEventList.class);
		return list;
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

	public void addThrow(Map<JSONThrowsURI, JSONThrowsList> throwMap, Pawn pawn, Dice roll) {

		for (JSONThrowsURI uri : throwMap.keySet()) {
			if (uri.getRollUri().contains(getID(pawn.getPawnUri()))) {
				throwMap.get(uri).addThrow(roll);
			}
		}
	}

	public void registerPlacesToBroker(Board b, String gameid) {
		
		
	}

	public void createNewBroker(String broker, JSONGameURI game) {
		
		HttpService.post(broker, game, HttpURLConnection.HTTP_OK);
		
	}
}
