package vs.jonas.client.json;

import java.util.List;

public class Board {

	String id;
	List<Field> fields;
	List<Integer> positions;
	String players; //uri
	
	public Board(String id, List<Field> fields, List<Integer> positions, String players) {
		super();
		this.id = id;
		this.fields = fields;
		this.positions = positions;
		this.players = players;
	}

	public String getId() {
		return id;
	}

	public List<Field> getFields() {
		return fields;
	}

	public List<Integer> getPositions() {
		return positions;
	}
	
	
}

