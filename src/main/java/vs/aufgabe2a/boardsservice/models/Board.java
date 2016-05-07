package vs.aufgabe2a.boardsservice.models;

import java.util.ArrayList;
import java.util.List;

import vs.aufgabe1.Validable;
import vs.aufgabe2a.boardsservice.models.json.JSONBoard;
import vs.aufgabe2a.boardsservice.models.json.JSONField;
import vs.aufgabe2a.boardsservice.models.json.JSONObject;

public class Board implements Convertable, Validable{
	
	private String id; // URI of the Board
	private List<Field> fields; // Fields on the Board
	private List<Integer> positions; // Positions on the board related to the players list
	private String players; // Uri zur Liste der Spieler auf dem Board
	
	
	public Board(){
		
		this(null);
	}
	
	public Board(String id){
		
		fields = new ArrayList<>();
		positions = new ArrayList<>();
		this.id = id;
	}

	public String getUri() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Integer> getPositions() {
		return positions;
	}

	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

	public String getPlayers() {
		return players;
	}

	public void setPlayers(String players) {
		this.players = players;
	}

	@Override
	public JSONObject convert() {
		
		List<JSONField> jsonFields = new ArrayList<>();
		for(Field f: this.getFields()){
			jsonFields.add((JSONField) f.convert()); // Zusichern da convert() JSONObject liefert
		}
		return new JSONBoard(this.getUri(), jsonFields, this.positions, this.players);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	public void addPawn(Pawn p) {
		
		this.fields.get(0).getPawns().add(p);
		
	}

	@Override
	public int hashCode() {
		
		return this.getUri().hashCode()  * 42;
	}

	@Override
	public boolean equals(Object obj) {

		if(obj == null){
			return false;
		}
		
		if(obj instanceof Board){
			Board b = (Board) obj;
			return b.getUri().equals(this.getUri());
		}
		
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}	
	
	
	
}
