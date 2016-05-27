package vs.jan.helper.brokerservice;

import java.util.Map;

import vs.jan.exception.ResourceNotFoundException;
import vs.jan.helper.Helper;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;

public class BrokerHelper extends Helper {
	
	public Broker getBroker(Map<Broker, JSONGameURI> brokers, String gameid) {
		
		for(Broker b: brokers.keySet()){
			if(b.getUri().contains(gameid)){
				return b;
			}
		}
		
		throw new ResourceNotFoundException(Error.BROKER_NOT_FOUND.getMsg());
	}

	public Place getPlace(Map<Broker, JSONGameURI> brokers, String placeid) {
		
		for(Broker b: brokers.keySet()){
			for(Place p: b.getPlaces()){
				if(p.getUri().contains(placeid)){
					return p;
				}
			}
		}
		
		throw new ResourceNotFoundException(Error.PLACE_NOT_FOUND.getMsg());
	}


}
