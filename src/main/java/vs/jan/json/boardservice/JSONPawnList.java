package vs.jan.json.boardservice;

import java.util.ArrayList;
import java.util.List;

public class JSONPawnList {

	List<String> pawns;

	public JSONPawnList() {
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
