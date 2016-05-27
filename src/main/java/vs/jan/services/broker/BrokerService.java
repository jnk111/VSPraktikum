package vs.jan.services.broker;

import java.util.Map;

import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.brokerservice.BrokerHelper;
import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.json.brokerservice.JSONBrokerList;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.json.brokerservice.JSONOwner;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.ServiceList;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.validator.BrokerValidator;
import vs.jan.validator.Validator;

public class BrokerService {
		
	private Validator validator;
	private Map<Broker, JSONGameURI> brokers;
	private ServiceList services;
	private BrokerHelper helper;
	
	
	
	public BrokerService(){
		this.validator = new BrokerValidator();
		this.helper = new BrokerHelper();
	};

	public void createBroker(JSONGameURI game, String host) throws ResponseCodeException {
		validator.checkJsonIsValid(game, Error.JSON_GAME_URI.getMsg());
		Broker b = new Broker("/broker/" + helper.getID(game.getURI()));
		b.setGameUri(game.getURI());
		brokers.put(b, game);
		this.services = ServiceAllocator.initServices(host, host);
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
		Broker b = helper.getBroker(this.brokers, gameid);
		b.setName(broker.getName());
	}

	public void registerPlace(String gameid, String placeid, JSONPlace place) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkJsonIsValid(place, Error.JSON_PLACE.getMsg());
		Broker broker = helper.getBroker(this.brokers, gameid);
		Place p = new Place(place.getPlace());
		p.update(place);
		String id = helper.getID(place.getPlace());
		place.setId("/broker/places/" + id);
		broker.addPlace(p);
	}

	public ServiceList getServices() {
		return services;
	}

	public void setServices(ServiceList services) {
		this.services = services;
	}

	public void visitPlace(String gameid, String placeid, String pawnid, String playeruri) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerUriIsValid(playeruri, Error.PLAYER_URI.getMsg());
		Broker broker = helper.getBroker(brokers, gameid);
		Place place = broker.getPlace(placeid);
		
		if(place.getOwner() != null){
			
			/* Zahle Miete an Owner
			 * --------------------
			 * pruefen ob genug Geld
			 * Wenn nicht, Kredit
			 * zahlen
			 */
		}
		
	}

	public JSONBroker getSpecificBroker(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		Broker b = helper.getBroker(this.brokers, gameid);
		return b.convert();
	}

	public JSONPlace getSpecificPlace(String placeid) {
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		Place place = helper.getPlace(this.brokers, placeid);
		return place.convert();
	}

	public JSONOwner getOwner(String placeid) {
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		Place p = helper.getPlace(brokers, placeid);
		JSONOwner owner = new JSONOwner();
		
		if(p.getOwner() != null){
			owner = p.getOwner().convert();
		}
		
		return owner;
		
	}

}
