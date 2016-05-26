package vs.jonas.client.json;

import java.util.ArrayList;
import java.util.List;

public class PawnList {

	List<String> pawns;

	public PawnList() {
		pawns = new ArrayList<>();
	}

	public void addPawnURI(String uri) {

		pawns.add(uri);
	}

	public List<String> getPawns() {
		return pawns;
	}

	public void setPawns(List<String> pawns) {
		this.pawns = pawns;
	}
}
