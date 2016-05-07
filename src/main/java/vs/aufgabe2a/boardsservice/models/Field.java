package vs.aufgabe2a.boardsservice.models;

import java.util.ArrayList;
import java.util.List;

import vs.aufgabe1.Validable;
import vs.aufgabe2a.boardsservice.models.json.JSONField;
import vs.aufgabe2a.boardsservice.models.json.JSONObject;

public class Field implements Convertable, Validable{
	
	private Place place;
	private List<Pawn> pawns; // The List of pawns on the board, e. g. e.g. ['/boards/41/pawns/mario' , ...]
	
	public Field(){
		
		pawns = new ArrayList<>();
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
	public JSONObject convert() {
		
		String place = this.getPlace().getPlaceUri();
		List<String> pawnUris = new ArrayList<>();
		for(Pawn p: this.getPawns()){
			pawnUris.add(p.getPawnUri());
		}
		
		return new JSONField(place, pawnUris);
		
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int hashCode() {
		
		return (this.getPlace().hashCode() + this.getPawns().hashCode()) * 42;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(obj instanceof Field){
			Field f = (Field) obj;
			
			return this.getPlace().equals(f.getPlace()) 
					&& this.getPawns().equals(f.getPawns());
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	
	
	
	
	

}
