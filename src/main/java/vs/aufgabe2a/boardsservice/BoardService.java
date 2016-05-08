package vs.aufgabe2a.boardsservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vs.aufgabe2a.boardsservice.exceptions.InvalidInputException;
import vs.aufgabe2a.boardsservice.exceptions.ResourceNotFoundException;
import vs.aufgabe2a.boardsservice.models.Board;
import vs.aufgabe2a.boardsservice.models.Field;
import vs.aufgabe2a.boardsservice.models.Pawn;
import vs.aufgabe2a.boardsservice.models.Place;
import vs.aufgabe2a.boardsservice.models.json.JSONBoard;
import vs.aufgabe2a.boardsservice.models.json.JSONBoardList;
import vs.aufgabe2a.boardsservice.models.json.JSONField;
import vs.aufgabe2a.boardsservice.models.json.JSONGameURI;
import vs.aufgabe2a.boardsservice.models.json.JSONPawn;
import vs.aufgabe2a.boardsservice.models.json.JSONPawnList;
import vs.aufgabe2a.boardsservice.models.json.JSONPlace;
import vs.aufgabe2a.boardsservice.models.json.JSONThrowsList;
import vs.aufgabe2a.boardsservice.models.json.JSONThrowsURI;

public class BoardService {

	/*
	 * Mapping Board -> GameUri
	 */
	private Map<Board, JSONGameURI> boards;
	
	/*
	 * Uri-Liste der gemachten Wuerfe, dient zur Ueberpruefung
	 * JSONThrowsUri -> die URI der von einer Pawn gemachten Wuerfe
	 * JSONThrowsList -> die Werte der Wuerfel
	 */
	private Map<JSONThrowsURI, JSONThrowsList> throwMap;
	

	/**
	 * Defaultkonstruktor
	 */
	public BoardService() {
		boards = new HashMap<>();
	}

	
	/**
	 * Liefert alle Board-Uris, die dem Spiel zugeteilt wurden
	 * @return	
	 * 				Liste der Board-Uris als JSON-DTO
	 */
	public JSONBoardList getAllBoardURIs() {
		
		JSONBoardList boardURIs = new JSONBoardList();
		boards.forEach((k, v) -> boardURIs.addBoardURI(k.getUri()));
		return boardURIs;
	}

	
	/**
	 * Erzeugt ein neues Board fuer die uebergebene Gameid, falls
	 * dieses Spiel vorhanden ist.
	 * @param game	
	 * 				Das Spiel, fuer das ein Board erzeugt werden soll
	 * @throws InvalidInputException 
	 * 				Json-Format der Uri ungueltig
	 */
	public synchronized void createNewBoard(JSONGameURI game) 
			throws InvalidInputException{
		
		if(game.isValid()){
			String boardUri = "/boards/" + game.getURI().split("/")[2];
			boards.put(new Board(boardUri), game);
		}else{
			throw new InvalidInputException();
		}
	}


	/**
	 * Liefert das einer Gameid zugehoerige Board, falls vorhanden
	 * @param gameid	
	 * 				Die Gameid
	 * @return	
	 * 				Das zugehoerige Board als JSON-DTO
	 * @throws ResourceNotFoundException	
	 * 				Board wurde nicht gefunden
	 */
	public JSONBoard getBoardForGame(String gameid) 
			throws ResourceNotFoundException{

		Board b = getBoard(gameid);
		
		if(b != null){
			return (JSONBoard) getBoard(gameid).convert();
		}
		throw new ResourceNotFoundException();

	}

	
	/**
	 * Erzeugt eine neue Spiefigur auf dem Board
	 * @param pawn		D
	 * 				Die Figur als JSON-DTO
	 * @param gameid	
	 * 				Das Spiel fuer das die Figur erzeugt werden soll
	 * @throws InvalidInputException		
	 * 				Ungueltige JSON-DTO (z. B. required-Parameter fehlen)
	 * @throws ResourceNotFoundException	
	 * 				Es konnte kein Board zum Spiel gefunden werden
	 */
	public synchronized void createNewPawnOnBoard(JSONPawn pawn, String gameid) 
			throws InvalidInputException, ResourceNotFoundException{

		Board b = getBoard(gameid);
		System.out.println(b);
		
		if(b != null){
			if(pawn.isValid()){
				Pawn p =  new Pawn();
				String pawnUri = getPawnUri(b, pawn.getPlayer());
				p.setPawnUri(pawnUri);
				p.setMovesUri(p.getPawnUri() + "/move");
				p.setPlaceUri(pawn.getPlace());						// Annahme required
				p.setPlayerUri(pawn.getPlayer());					// Annahme required
				p.setPosition(pawn.getPosition());				// Annahme required
				p.setRollsUri(p.getPawnUri() + "/roll");
				b.addPawn(p);
			}else{
				throw new InvalidInputException();
			}
		}else{
			throw new ResourceNotFoundException();
		}

	}

	/**
	 * TODO: implementiere URI-Erzeugung
	 * Generiert die Pawn-Uri aus dem GET-Response der PlayerUri
	 * @param board		
	 * 						The Board
	 * @param playerUri		
	 * 								The PlayerUri
	 * @return 	
	 * 				Pawn-Uri
	 */
	private String getPawnUri(Board board, String playerUri) {
		
		// GET playerUri
		String boardUri = board.getUri();
		return boardUri + "/pawns/" + ((int) (Math.random() * 20));
		
	}

	/**
	 * Liefert alle Pawn-Uris, die auf dem Spielbrett sind
	 * @param 
	 * 				gameid	Die Gameid des Boards
	 * @return		
	 * 				Liste von Pawn-Uris als JSON-Dto
	 * @throws 
	 * 				ResourceNotFoundException	Board wurde nicht gefunden
	 */
	public JSONPawnList getPawnsOnBoard(String gameid) 
			throws ResourceNotFoundException { 
		
		JSONPawnList pl = new JSONPawnList();
		Board b = getBoard(gameid);
		
		if(b != null){
			for(Field f: b.getFields()){
				for(Pawn p: f.getPawns()){
					pl.addPawnURI(p.getPawnUri());
				}
			}
			return pl;
		}
		throw new ResourceNotFoundException();		
	}

	/**
	 * Liefert eine bestimmte Spielfigur als JSON-DTO
	 * 
	 * @param gameid	
	 * 				Die Gameid des Boards auf dem die Figur steht
	 * @param pawnid	
	 * 				Die Pawn-ID um die Figur eindeutig zu identifizieren
	 * @return		
	 * 				Die Pwan, als JSON-DTO
	 * @throws 
	 * 				ResourceNotFoundException		Board oder Spielfigur konnte nicht gefunden werden
	 */
	public JSONPawn getSpecificPawn(String gameid, String pawnid) 
			throws ResourceNotFoundException{

		Board b = getBoard(gameid);
		if(b != null){
			for(Field f: b.getFields()){
				for(Pawn p: f.getPawns()){
					if(p.getPawnUri().contains(pawnid)){
						return (JSONPawn) p.convert();
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	
	/**
	 * Liefert alle Wuerfe die von einem Spieler fuer seine Figur bereits gemacht wurden,
	 * als Json-DTO
	 * @param gameid
	 * 				Die Gameid des Boards
	 * @param pawnid
	 * 				Die Pawnid der Figur
	 * @return JSONThrowsList
	 * 				Die Liste der Wuerfe zu dieser Figur als Json-DTO
	 * @throws ResourceNotFoundException
	 * 				Board oder Pawn nicht gefunden
	 */
	public JSONThrowsList getDiceThrows(String gameid, String pawnid) 
			throws ResourceNotFoundException {

		Board b = getBoard(gameid);
		
		if(b != null){
			for(JSONThrowsURI uri: throwMap.keySet()){
				if(uri.getRollUri().contains(pawnid)){
					return throwMap.get(uri);
				}
			}
		}
		
		throw new ResourceNotFoundException();
	}


	/**
	 * Liefert das Board zu einer Gameid, muss spaeter erweitert werden,
	 * falls mehere Boards einem Spiel zugeteilt werden koennen.
	 * @param gameid
	 * 				Die Gameid des Boards
	 * @return Board
	 * 				Das Board zu der Gameid 			
	 */
	private Board getBoard(String gameid) {

		for(Board b: boards.keySet()){
			if(boards.get(b).getURI().contains(gameid)){
				return b;
			}
		}
		return null;
	}


	/**
	 * Ermittelt alle Felder auf dem Board, erzeugt jeweils URI und gibt
	 * eine Liste mit ermittelten URis zurueck.
	 * @param gameid
	 * 				Die Gameid des Boards
	 * @return List<String>
	 * 				 Die Uri-Liste der Felder auf dem Board
	 * @throws ResourceNotFoundException
	 * 					Das Board wurde nicht gefunden
	 */
	public List<String> getAllPlaces(String gameid) 
			throws ResourceNotFoundException {
		
		Board b = getBoard(gameid);
		if (b != null){
			List<String> allPlaceURIs = new ArrayList<>();
			
			for(Field f: b.getFields()){
				allPlaceURIs.add(f.getPlace().getPlaceUri());
			}
			return allPlaceURIs;
		}
		throw new ResourceNotFoundException();		
	}

	
	/**
	 * Gibt Informationen ueber einen bestimmten Place zurueck 
	 * (Das ist nicht das gesamte Feld, sondern nur die dem Feld zugeteilte Straße)
	 * 
	 * @param gameid
	 * 					Die Gameid des Boards
	 * @param placeid
	 * 					Die Placeid
	 * @return JSONPlace
	 * 					Json-DTO des Places
	 * 				
	 * @throws ResourceNotFoundException
	 * 					Board oder Place nicht gefunden
	 * 					
	 */
	public JSONPlace getSpecificPlace(String gameid, String placeid) 
			throws ResourceNotFoundException {
		
		Board b = getBoard(gameid);
		if(b != null){
			for(Field f: b.getFields()){
				if(f.getPlace().getPlaceUri().contains(placeid)){
					return (JSONPlace) f.getPlace().convert();
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Bewegt eine Spielfigur um den Wert der <code>rollValue</code> vorwaerts
	 * @param gameid
	 * 				Die Gameid des Boards
	 * @param pawnid
	 * 				Die Pawn-ID der Figur
	 * @param rollValue
	 * 				Der Wert um den die Figur bewegt werden soll
	 * @throws ResourceNotFoundException
	 * 				Board oder Figur nicht gefunden
	 */
	public synchronized void movePawn(String gameid, String pawnid, int rollValue) 
			throws ResourceNotFoundException	{
		
		Board b = getBoard(gameid);
		
		if(b != null){
			for(Field f: b.getFields()){
				for(Pawn p: f.getPawns()){
					if(p.getPawnUri().contains(pawnid)){
						p.setPosition(p.getPosition() + rollValue);
						return;
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}


	/**
	 * Uebergibt einen Wurf an das Board und fuehrt weitere noetige Aktonen aus.
	 * @param gameid
	 * 				Das Board der Gameid
	 * @param pawnid
	 * 				Die Pawn-Id der Figur, fuer die gewuerfelt wird
	 * @throws ResourceNotFoundException
	 * 				Board oder Figur nicht gefunden
	 */
	public synchronized void rollDice(String gameid, String pawnid) 
			throws ResourceNotFoundException{
		
		// call Dice-Service with player and gameuri
		// add Dice to Throwmap related to the rolls uri of the pawnid
		// move the pawn to with the value of the dice 
		// update player position and place#
		
		// do further needed actions ...
		
		// retrieve Eventlist associated with this throw
			
	}

	/**
	 * Loescht eine Spielfigur vom Board (z. B. bei verlassen des Spiels vor Ende)
	 * @param gameid
	 * 				Die dem Board zugeteilte Gameid
	 * @param pawnid
	 * 				Die Pawn-ID der Figur, die geloescht werden soll
	 * @throws ResourceNotFoundException
	 * 				Baord oder Figur nicht gefunden
	 */
	public synchronized void deletePawnFromBoard(String gameid, String pawnid) 
			throws ResourceNotFoundException {
		
		Board b = getBoard(gameid);
		if(b != null){
			for(Field f: b.getFields()){
				for(Pawn p: f.getPawns()){
					if(p.getPawnUri().contains(pawnid)){
						f.getPawns().remove(p);
						return;
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Loescht das Board bei Beendigung des Spiels
	 * @param gameid
	 * 				Die Gameid des Boards
	 * @throws ResourceNotFoundException
	 * 				Das Board wurde nicht gefunden
	 */
	public synchronized void deleteBoard(String gameid) 
			throws ResourceNotFoundException {
		
		Board b = getBoard(gameid);
		
		if(b != null){
			boards.remove(b);
			return;
		}
		
		throw new ResourceNotFoundException();
		
	}

	/**
	 * Verandert Feldinformationen auf dem Board (z. B. eine Straße erhaelt einen neue Broker-Uri)
	 * @param place
	 * 				Der Place als Json-DTO
	 * @param pathinfo
	 * 				die Uri des Feldes
	 * @param gameid
	 * 				Die Gameid des Boardes
	 * @throws ResourceNotFoundException
	 * 					Board oder Place nicht gefunden
	 */
	public synchronized void updateAPlaceOnTheBoard(JSONPlace place, String pathinfo, String gameid) 
			throws ResourceNotFoundException{
		
		Board key = getBoard(gameid);
		if(place.isValid() && key != null){
			for(Field f: key.getFields()){
				Place p = f.getPlace();
				if(p.getPlaceUri().equals(pathinfo)){
					p.setName(place.getName());
					p.setBrokerUri(place.getBroker());
					return;
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	
	/**
	 * Befuellt ein Board mit Feldern
	 * @param gameid
	 * 				Die Gameid des Boards
	 * @param board
	 * 				Das Board als Json-DTO
	 * @throws ResourceNotFoundException
	 * 				Das Board wurde nicht gefunden
	 */
	public synchronized void placeABoard(String gameid, JSONBoard board) 
			throws ResourceNotFoundException{
		
		Board key = getBoard(gameid);
		
		if(board.isValid() && key != null){
			List<Pawn> pawns = new ArrayList<>();
			List<Field> fields = new ArrayList<>();
			Pawn pawn = null;
			
			for(JSONField f: board.getFields()){
				Field field = new Field();
				pawns = new ArrayList<>();
				for(String pawnUri: f.getPawns()){
					pawn = new Pawn();
					pawn.setPawnUri(pawnUri);
					pawn.setPlaceUri(f.getPlace());
					pawn.setMovesUri(pawnUri + "/move");
					pawn.setPosition(0);								// Startposition
					pawn.setRollsUri(pawnUri + "/roll");
					pawns.add(pawn);
				}
				Place place = new Place();
				place.setPlaceUri(f.getPlace());
				field.setPawns(pawns);
				field.setPlace(place);
				fields.add(field);
			}
			
			key.setFields(fields);
			key.setPlayers("/boards/" + gameid + "/players");
			key.setPositions(board.getPositions());									// TODO: nachfragen
			
			JSONGameURI entry = boards.get(key);
			boards.put(key, entry);
		}else{
			throw new InvalidInputException();
		}
	}


	/**
	 * Weist einer Figur ein neues Feld zu (z. B. nach Wuerfeln)
	 * @param gameid
	 * 				Die Gameid des Boardes
	 * @param pawn
	 * 				Die Figur als Json-DTO
	 * @throws ResourceNotFoundException
	 * 				Das Board oder die Figur wurde nicht gefunden
	 */
	public synchronized void placeAPawn(String gameid, JSONPawn pawn) 
			throws ResourceNotFoundException {
		
		Board b = getBoard(gameid);
		
		if(pawn.isValid() && b != null){
			for(Field f: b.getFields()){
				for(Pawn p2: f.getPawns()){
					if(p2.getPawnUri().contains(pawn.getId())){
						p2.setMovesUri(pawn.getMove());
						p2.setPawnUri(pawn.getId());
						p2.setPlaceUri(pawn.getPlace());
						p2.setPlayerUri(pawn.getPlayer());
						p2.setPosition(pawn.getPosition());
						p2.setRollsUri(pawn.getRoll());
						return;
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}
}
