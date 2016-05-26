package vs.jan.json.boardservice;

import java.util.ArrayList;
import java.util.List;

public class JSONPlaceList {

	private List<JSONPlace> places;

	public JSONPlaceList() {
		places = new ArrayList<>();
	}

	public void addPlace(JSONPlace p) {

		places.add(p);
	}

	public List<JSONPlace> getPlaces() {
		return places;
	}

	public void setPlaces(List<JSONPlace> places) {
		this.places = places;
	}

}
