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
import vs.aufgabe2a.boardsservice.models.Throws;
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

	private Map<Board, JSONGameURI> boards;
	private Map<JSONThrowsURI, JSONThrowsList> throwMap;
	private Throws rolls;

	public BoardService() {
		boards = new HashMap<>();
		rolls = new Throws();
	}

	public JSONBoardList getAllBoardURIs() {
		
		JSONBoardList boardURIs = new JSONBoardList();
		boards.forEach((k, v) -> boardURIs.addBoardURI(k.getUri()));
		return boardURIs;
	}

	
	public synchronized void createNewBoard(JSONGameURI game) {

		if(game.isValid()){
			String boardUri = "/boards/" + game.getURI().split("/")[2];
			boards.put(new Board(boardUri), game);
		}else{
			throw new InvalidInputException();
		}
	}

	public JSONBoard getBoardForGame(String gameid) {

		Board b = getBoard(gameid);
		
		if(b != null){
			return (JSONBoard) getBoard(gameid).convert();
		}
		throw new ResourceNotFoundException();

	}

	public synchronized void createNewPawnOnBoard(JSONPawn pawn, String gameid) {

		Board b = getBoard(gameid);
		System.out.println(b);
		
		if(pawn.isValid() && b != null){
			
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

	}

	/**
	 * TODO: implement
	 * Generates the Pawn-Uri from the GET-Response of the given playerUri
	 * @param b		The Board
	 * @param playerUri		The PlayerUri
	 * @return 	Pawn-Uri
	 */
	private String getPawnUri(Board b, String playerUri) {
		
		String boardUri = b.getUri();
		return boardUri + "/pawns/" + ((int) (Math.random() * 20));
		
	}

	public JSONPawnList getPawnsOnBoard(String gameid) {
		
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

	public JSONPawn getSpecificPawn(String gameid, String pawnid) {

		Board b = getBoard(gameid);
		for(Field f: b.getFields()){
			for(Pawn p: f.getPawns()){
				if(p.getPawnUri().contains(pawnid)){
					return (JSONPawn) p.convert();
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	public JSONThrowsList getDiceThrows(String gameid, String pawnid) {

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

	public Throws getRolls() {
		return rolls;
	}

	public void setRolls(Throws rolls) {
		this.rolls = rolls;
	}

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
	 * @param gameid	Die Id zum Spiel
	 * @return Liste mit URIs
	 */
	public List<String> getAllPlaces(String gameid) {
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

	public JSONPlace getSpecificPlace(String gameid, String placeid) {
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

	

	public synchronized void movePawn(String gameid, String pawnid, int rollValue) {
		
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
	 * TODO: implement
	 * @param gameid
	 * @param pawnid
	 * @return
	 */
	public synchronized void rollDice(String gameid, String pawnid) {
		
		// call Dice-Service with player and gameuri
		// add Dice to Throwmap related to the rolls uri of the pawnid
		// move the pawn to with the value of the dice 
		// update player position and place#
		
		// do further needed actions ...
		
		// retrieve Eventlist associated with this throw
			
	}

	public synchronized void deletePawnFromBoard(String gameid, String pawnid) {
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

	public synchronized void deleteBoard(String gameid) {
		
		Board b = getBoard(gameid);
		
		if(b != null){
			boards.remove(b);
			return;
		}
		
		throw new ResourceNotFoundException();
		
	}

	/**
	 * TODO: Documentation
	 * @param place
	 * @param pathinfo
	 * @param gameid
	 * @return
	 */
	public synchronized void placeANewPlaceOnTheBoard(JSONPlace place, String pathinfo, String gameid) {
		Board key = getBoard(gameid);
		if(place.isValid() && key != null){
			for(Field f: key.getFields()){
				Place p = f.getPlace();
				if(p.getPlaceUri().equals(pathinfo)){
					p.setName(place.getName());
					p.setBrokerUri(place.getBroker());
					p.setPlaceUri(getPlaceUri(key, p));
					return;
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	
	/**
	 * TODO: implement
	 * Generates the Place-Uri for a given board and place
	 * @param board		the board
	 * @param place the related place
	 * @return
	 */
	private String getPlaceUri(Board board, Place place) {
		
		return board.getUri() + "/places/" + 0;
	}

	
	public synchronized void placeABoard(String gameid, JSONBoard b) {
		
		Board key = getBoard(gameid);
		
		if(b.isValid() && key != null){
			List<Pawn> pawns = new ArrayList<>();
			List<Field> fields = new ArrayList<>();
			Pawn pawn = null;
			
			for(JSONField f: b.getFields()){
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
			key.setPositions(b.getPositions());									// TODO: nachfragen
			
			JSONGameURI entry = boards.get(key);
			boards.put(key, entry);
		}else{
			throw new InvalidInputException();
		}
	}

	public synchronized void placeAPawn(String gameid, JSONPawn p) {
		
		Board b = getBoard(gameid);
		
		if(p.isValid() && b != null){
			for(Field f: b.getFields()){
				for(Pawn p2: f.getPawns()){
					if(p2.getPawnUri().equals(p.getId())){
						p2.setMovesUri(p.getMove());
						p2.setPawnUri(p.getId());
						p2.setPlaceUri(p.getPlace());
						p2.setPlayerUri(p.getPlayer());
						p2.setPosition(p.getPosition());
						p2.setRollsUri(p.getRoll());
						return;
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}

}
