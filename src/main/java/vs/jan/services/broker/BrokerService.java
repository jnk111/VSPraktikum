package vs.jan.services.broker;

import java.net.HttpURLConnection;
import java.util.Map;

import com.google.gson.Gson;

import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.json.brokerservice.JSONBrokerList;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.ServiceList;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.exception.Error;
import vs.jan.model.exception.ResponseCodeException;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.tools.HttpService;
import vs.jan.validator.Validator;

public class BrokerService {
	
	private final Gson GSON = new Gson();
	private Validator validator;
	private Map<Broker, JSONGameURI> brokers;
	private ServiceList services;
	
	
	public BrokerService(){
		this.validator = new Validator();
	};

	public void createBroker(JSONGameURI game, String host) throws ResponseCodeException {
		validator.checkJsonIsValid(game, Error.JSON_GAME_URI.getMsg());
		Broker b = new Broker("/broker/" + getID(game.getURI()));
		b.setGameUri(game.getURI());
		brokers.put(b, game);
		this.services = ServiceAllocator.initServices(host, host);
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkJsonIsValid(broker, Error.JSON_Broker.getMsg());
		
		
		
	}

	public void registerPlace(String gameid, String placeid, JSONPlace place) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkJsonIsValid(place, Error.JSON_PLACE.getMsg());
		
		
		
		
		
		
	}

	public ServiceList getServices() {
		return services;
	}

	public void setServices(ServiceList services) {
		this.services = services;
	}

}
