package vs.aufgabe2a.boardsservice.models.json;

import java.util.List;

public class JSONField implements JSONObject{
	
	private String place;					// Uri of the Place
	private List<String> pawns;	
	
	public JSONField(String place, List<String> pawns){
		
		this.place = place;
		this.pawns = pawns;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public List<String> getPawns() {
		return pawns;
	}

	public void setPawns(List<String> pawns) {
		this.pawns = pawns;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}
	
	

}
