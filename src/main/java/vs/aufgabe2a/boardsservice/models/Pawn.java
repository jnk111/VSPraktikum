package vs.aufgabe2a.boardsservice.models;

import vs.aufgabe1.Validable;
import vs.aufgabe2a.boardsservice.models.json.JSONObject;
import vs.aufgabe2a.boardsservice.models.json.JSONPawn;

public class Pawn  implements Convertable, Validable{
	
	
	private String pawnUri; // Uri of the Pawn
	private String placeUri; // The URI of the Place on the Board
	private String playerUri; // Uri to the playerresource
	private int position; // The Position on the Board
	private String rollsUri; // The URI to the rolls of the player
	private String movesUri; // The URI to the moves of the player
	
	public Pawn(){
		
	}
	
	

	public String getPlayerUri() {
		return playerUri;
	}



	public void setPlayerUri(String playerUri) {
		this.playerUri = playerUri;
	}



	public String getPawnUri() {
		return pawnUri;
	}

	public void setPawnUri(String pawnUri) {
		this.pawnUri = pawnUri;
	}

	public String getPlaceUri() {
		return placeUri;
	}

	public void setPlaceUri(String placeUri) {
		this.placeUri = placeUri;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getRollsUri() {
		return rollsUri;
	}

	public void setRollsUri(String rollsUri) {
		this.rollsUri = rollsUri;
	}

	public String getMovesUri() {
		return movesUri;
	}

	public void setMovesUri(String movesUri) {
		this.movesUri = movesUri;
	}

	@Override
	public JSONObject convert() {
		
		JSONPawn json = 
				new JSONPawn(this.pawnUri, 
						this.playerUri, 
						this.placeUri, 
						this.position, 
						this.rollsUri, 
						this.movesUri);
		
		return json;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}



	@Override
	public int hashCode() {
		
		return this.getPawnUri().hashCode() * 42;
	}



	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(obj instanceof Pawn){
			
			Pawn p = (Pawn) obj;
			return p.getPawnUri().equals(this.getPawnUri());
		}
		
		return false;
	}



	@Override
	public String toString() {
		
		return "Pawn-Uri: " + this.getPawnUri() + ", Player-Uri: " + this.getPlayerUri();
	}
	
	
	
	
	
	
	

}
