package vs.jan.json;

import java.util.ArrayList;
import java.util.List;

import vs.jan.model.Validable;

public class JSONField implements Validable{
	
	private String place;					// Uri of the Place
	private List<String> pawns;	
	
	public JSONField(String place, List<String> pawns){
		
		this.place = place;
		this.pawns = pawns;
	}
	
	public JSONField(String place){
		
		this.place = place;
		this.pawns = new ArrayList<>();
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

	/**
	 * Prueft ob das Ubergeben Field gueltig ist
	 * Keines der Felder darf null sein.
	 * Falls nicht alle benoetigt werden, sollte der entsprechende
	 * Konstruktor aufgerufen werden.
	 * 
	 * @return <code>true</code> : gueltig, <code>false</code> unguelitg
	 */
	@Override
	public boolean isValid() {

		return this.getPlace() != null
						&& this.getPawns() != null;
	}
	
	

}
