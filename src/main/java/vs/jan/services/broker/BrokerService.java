package vs.jan.services.broker;

import java.util.Map;

import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.json.brokerservice.JSONBrokerList;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.tools.HttpService;
import vs.jan.validator.brokerservice.BrokerValidator;

public class BrokerService {
	
	private BrokerValidator validator;
	private Map<Broker, JSONGameURI> brokers;
	
	public BrokerService(){
		validator = new BrokerValidator();
	};

	public void createBroker(JSONGameURI game) {
		validator.checkGameUriIsValid(game);
		Broker b = new Broker("/broker/" + getID(game.getURI()));
		b.setGameUri(game.getURI());
		brokers.put(b, game);
	}

	private String getID(String uri) {
		String [] u = uri.split("/");
		return u[u.length - 1];
	}

	public JSONBrokerList getBrokers() {
		JSONBrokerList list = new JSONBrokerList();
		for(Broker b: brokers.keySet()){
			list.getBroker().add(b.getUri());
		}
		return list;
	}

	public void placeBroker(String gameid, JSONBroker broker) {
		validator.checkIdIsNotNull(gameid);
		validator.checkJsonIsValid(broker);
		
		
		
	}

	public void registerPlace(String gameid, String placeid, JSONPlace place) {
		validator.checkIdIsNotNull(gameid);
		validator.checkIdIsNotNull(placeid);
		validator.checkJsonIsValid(place);
		
		
		
		
	}

}
