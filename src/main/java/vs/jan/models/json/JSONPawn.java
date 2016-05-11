package vs.jan.models.json;

import vs.jan.models.Validable;

public class JSONPawn implements Validable{
	private String id;		// Uri to the resource itself
	private String player; // Uri to the playerresource
	private String place; // Uri to the Place on the Board the player stands on
	private int position; // numeric position on the board
	private String roll; // Uri to the roll of the player
	private String move; // Uri to the moves of the player
	
	
	public JSONPawn(String id, String player, 
									String place, int position,
									String roll, String move){
		
		this.id = id;
		this.player = player;
		this.place = place;
		this.position = position;
		this.roll = roll;
		this.move = move;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getRoll() {
		return roll;
	}

	public void setRoll(String roll) {
		this.roll = roll;
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	/**
	 * Prueft ob die Ubergebene Figur gueltig ist
	 * Mindestes <code>place</code> 
	 * und <code>playerUri</code> duerfen nicht null sein.
	 * Die <code>id</code> wird vom Service vergeben.
	 * Falls nicht alle benoetigt werden, sollte der entsprechende
	 * Konstruktor aufgerufen werden.
	 * @return <code>true</code> : gueltig, <code>false</code> unguelitg
	 */
	@Override
	public boolean isValid() {
		
		return  this.getPlace() != null
								&& this.getPlayer() != null;
	}
}