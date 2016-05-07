package vs.aufgabe2a.boardsservice.models;

import java.util.List;

import vs.aufgabe1.Validable;
import vs.aufgabe2a.boardsservice.models.json.JSONPlace;

public class Place implements Convertable<JSONPlace>, Validable{
	
	private String name;
	private String brokerUri;
	private String placeUri; // Uri to the place on the board
	private List<Pawn> pawns; // Pawns on the Field of the place
	
	public Place(){
		this(null);
	}
	
	public Place(String placeUri){

		this(placeUri, null, null);
	}
	
	public Place(String placeUri, String name, String brokerUri){
		
		this.brokerUri = brokerUri;
		this.name = name;
		this.placeUri = placeUri;
	}

	public String getPlaceUri() {
		return placeUri;
	}

	public void setPlaceUri(String place) {
		this.placeUri = place;
	}

	public List<Pawn> getPawns() {
		return pawns;
	}

	public void setPawns(List<Pawn> pawns) {
		this.pawns = pawns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrokerUri() {
		return brokerUri;
	}

	public void setBrokerUri(String brokerUri) {
		this.brokerUri = brokerUri;
	}

	@Override
	public JSONPlace convert() {
		
		return new JSONPlace(this.name, this.brokerUri);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		
		return	(this.getBrokerUri().hashCode()
							+ this.getPlaceUri().hashCode()) * 42;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(obj instanceof Place){
			Place p = (Place) obj;
			
			return this.getBrokerUri().equals(p.getBrokerUri())
								&& this.getPlaceUri().equals(p.getPlaceUri());
		}
		return false;
	}

	@Override
	public String toString() {
		
		return "Name: " + this.name + ", URI: " 
						+ this.placeUri + ", Broker: " 
							+ this.brokerUri + ", Pawns: "
								+ this.getPawns().toString();
	}

}
