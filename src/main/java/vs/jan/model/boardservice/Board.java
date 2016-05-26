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

		return this.getUri().hashCode() * 42;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof Board) {
			Board b = (Board) obj;
			return b.getUri().equals(this.getUri());
		}

		return false;
	}

	@Override
	public String toString() {
		return super.toString();
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
