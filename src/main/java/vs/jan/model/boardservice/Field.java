package vs.jan.model.boardservice;

import java.util.ArrayList;
import java.util.List;

import vs.jan.json.boardservice.JSONField;
import vs.jan.model.Convertable;

public class Field implements Convertable<JSONField> {

	private Place place;
	private List<Pawn> pawns; // The List of pawns on the board, e. g. e.g.
														// ['/boards/41/pawns/mario' , ...]

	public Field() {

		this(null, new ArrayList<>());

	}

	public Field(Place p) {
		this(p, new ArrayList<>());
	}

	public Field(Place p, List<Pawn> pawns) {
		this.place = p;
		this.pawns = pawns;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public List<Pawn> getPawns() {
		return pawns;
	}

	public void setPawns(List<Pawn> pawns) {
		this.pawns = pawns;
	}

	@Override
	public JSONField convert() {

		String place = this.getPlace().getPlaceUri();
		List<String> pawnUris = new ArrayList<>();
		for (Pawn p : this.getPawns()) {
			pawnUris.add(p.getPawnUri());
		}

		return new JSONField(place, pawnUris);

	}

	@Override
	public int hashCode() {

		return (this.getPlace().hashCode() + this.getPawns().hashCode()) * 42;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof Field) {
			Field f = (Field) obj;

			return this.getPlace().equals(f.getPlace()) && this.getPawns().equals(f.getPawns());
		}
		return false;
	}

	@Override
	public String toString() {
		return this.place.toString();
	}

	public void removePawn(Pawn pawn) {
		this.pawns.remove(pawn);
	}

	public void addPawn(Pawn pawn) {
		this.pawns.add(pawn);
	}

}
