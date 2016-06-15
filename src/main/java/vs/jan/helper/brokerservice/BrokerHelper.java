package vs.jan.helper.brokerservice;

import java.util.Map;

import vs.jan.exception.ResourceNotFoundException;
import vs.jan.helper.Helper;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;

public class BrokerHelper extends Helper {

	public static Broker getBroker(Map<String, Broker> brokers, String gameid) {

		Broker b = brokers.get(gameid);

		if (b != null) {
			return b;
		}

		throw new ResourceNotFoundException(Error.BROKER_NOT_FOUND.getMsg());
	}

	public static Place getPlace(Broker b, String placeid) {

		for (Place p : b.getPlaces()) {
			String id = getID(p.getPlaceUri());
			if (id.equals(placeid)) {
				return p;
			}
		}

		throw new ResourceNotFoundException(Error.PLACE_NOT_FOUND.getMsg());
	}

}
