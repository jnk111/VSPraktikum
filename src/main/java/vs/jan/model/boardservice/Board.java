package vs.jan.model.boardservice;

import java.util.ArrayList;
import java.util.List;

import vs.jan.json.boardservice.JSONBoard;
import vs.jan.json.boardservice.JSONField;
import vs.jan.model.Convertable;

public class Board implements Convertable<JSONBoard> {

	private String id; // URI of the Board
	private List<Field> fields; // Fields on the Board
	private List<Integer> positions; // Positions on the board related to the
																		// players list
	private String players; // Uri zur Liste der Spieler auf dem Board

	public Board() {

		this(null);
	}

	public Board(String id) {

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
	public JSONBoard convert() {

		List<JSONField> jsonFields = new ArrayList<>();
		for (Field f : this.getFields()) {
			jsonFields.add((JSONField) f.convert()); // Zusichern da convert()
																								// JSONObject liefert
		}
		return new JSONBoard(this.getUri(), jsonFields, this.positions, this.players);
	}

	public void addNewPawn(Pawn p) {

		Field f = this.fields.get(0);
		f.getPawns().add(p);

		if (!this.positions.contains(new Integer(0))) {
			this.positions.add(0);
		}

	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((players == null) ? 0 : players.hashCode());
		result = prime * result + ((positions == null) ? 0 : positions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (players == null) {
			if (other.players != null)
				return false;
		} else if (!players.equals(other.players))
			return false;
		if (positions == null) {
			if (other.positions != null)
				return false;
		} else if (!positions.equals(other.positions))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return "Board [id=" + id + ", fields=" + fields + ", positions=" + positions + ", players=" + players + "]";
	}
	
	

	public void updatePositions(int oldPos, int newPos) {

		if (this.getFields().get(oldPos).getPawns().isEmpty()) {
			this.positions.remove(new Integer(oldPos));
		}

		if (!this.positions.contains(new Integer(newPos)) && newPos > -1) {
			this.positions.add(newPos);
		}
	}

	public boolean hasFields() {

		return this.fields != null && !this.fields.isEmpty();
	}

	public void removePawn(Pawn p) {
		for (Field f : this.fields) {
			if (f.getPawns().contains(p)) {
				f.getPawns().remove(p);
			}
		}

	}

	public void addField(Field field) {
		this.fields.add(field);
	}

	public void reloadPositions() {

		this.positions = new ArrayList<>();
		for (int i = 0; i < this.fields.size(); i++) {
			Field f = this.fields.get(i);

			if (!f.getPawns().isEmpty() && !this.positions.contains(new Integer(f.getPlace().ordinal()))) {
				this.positions.add(f.getPlace().ordinal());
			}
		}
	}
}
