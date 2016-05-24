package vs.jan.json;

import java.util.ArrayList;
import java.util.List;

public class JSONBoard {

	private String id; // Uri zum Board
	private List<JSONField> fields; // Uris to Places on the Board
	private List<Integer> positions; // Positions on the Board
	private String players; // Uri to the playerlist on the board

	public JSONBoard() {

		this(null, null);
	}

	public JSONBoard(String id) {
		this(id, null);
	}

	public JSONBoard(String id, String players) {

		this.id = id;
		this.fields = new ArrayList<>();
		this.positions = new ArrayList<>();
		this.players = players;

	}

	public JSONBoard(String id, List<JSONField> fields, List<Integer> positions, String players) {

		this.id = id;
		this.fields = fields;
		this.positions = positions;
		this.players = players;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<JSONField> getFields() {
		return fields;
	}

	public void setFields(List<JSONField> fields) {
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

	/**
	 * Prueft ob das Ubergeben Board gueltig ist Keines der Felder darf null sein.
	 * Falls nicht alle benoetigt werden, sollte der entsprechende Konstruktor
	 * aufgerufen werden.
	 * 
	 * @return <code>true</code> : gueltig, <code>false</code> unguelitg
	 */
	public boolean isValid() {

		return this.getId() != null && this.getFields() != null;

	}

}
