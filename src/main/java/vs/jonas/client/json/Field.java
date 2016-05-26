package vs.jonas.client.json;

import java.util.List;

public class Field {

	String place;
	List<String> pawns;
	
	public Field(String place, List<String> players) {
		super();
		this.place = place;
		this.pawns = players;
	}

	public String getPlace() {
		return place;
	}

	public List<String> getPawns() {
		return pawns;
	} 
	
	
}
