package vs.jonas.client.json;

public class Pawn {
	private String id; // Uri to the resource itself
	private String player; // Uri to the playerresource
	private String place; // Uri to the Place on the Board the player stands on
	private int position; // numeric position on the board
	private String roll; // Uri to the roll of the player
	private String move; // Uri to the moves of the player

	public Pawn(String player) {
		this(null, player, null, 0, null, null);
	}

	public Pawn(String id, String player, String place, int position, String roll, String move) {

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
}
