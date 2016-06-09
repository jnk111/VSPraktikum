package vs.jan.json.boardservice;

import vs.jan.model.Validable;

public class JSONPawn implements Validable {
	private String id; // Uri to the resource itself
	private String player; // Uri to the playerresource
	private String place; // Uri to the Place on the Board the player stands on
	private int position; // numeric position on the board
	private String roll; // Uri to the roll of the player
	private String move; // Uri to the moves of the player

	public JSONPawn(String player) {
		this(null, player, null, 0, null, null);
	}

	public JSONPawn(String id, String player, String place, int position, String roll, String move) {

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
		return this.player;
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
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((move == null) ? 0 : move.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + position;
		result = prime * result + ((roll == null) ? 0 : roll.hashCode());
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
		JSONPawn other = (JSONPawn) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (move == null) {
			if (other.move != null)
				return false;
		} else if (!move.equals(other.move))
			return false;
		if (place == null) {
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (position != other.position)
			return false;
		if (roll == null) {
			if (other.roll != null)
				return false;
		} else if (!roll.equals(other.roll))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return "JSONPawn [id=" + id + ", player=" + player + ", place=" + place + ", position=" + position + ", roll="
				+ roll + ", move=" + move + "]";
	}

	/**
	 * Prueft ob die Ubergebene Figur gueltig ist Mindestes <code>place</code> und
	 * <code>playerUri</code> duerfen nicht null sein. Die <code>id</code> wird
	 * vom Service vergeben. Falls nicht alle benoetigt werden, sollte der
	 * entsprechende Konstruktor aufgerufen werden.
	 * 
	 * @return <code>true</code> : gueltig, <code>false</code> unguelitg
	 */
	@Override
	public boolean isValid() {
		return this.getPlayer() != null;
	}
}
