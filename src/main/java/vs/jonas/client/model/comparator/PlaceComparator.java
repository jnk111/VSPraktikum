package vs.jonas.client.model.comparator;

import java.util.Comparator;

import vs.jonas.client.json.Place;

public class PlaceComparator implements Comparator<Place> {

	@Override
	public int compare(Place o1, Place o2) {
		int i1 = o1.getValue();
		int i2 = o2.getValue();
		return Integer.compare(i1, i2);
	}

}
