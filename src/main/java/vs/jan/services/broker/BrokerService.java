package vs.jan.services.broker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.brokerservice.BrokerHelper;
import vs.jan.json.boardservice.JSONEvent;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.json.brokerservice.JSONAccount;
import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.json.brokerservice.JSONBrokerList;
import vs.jan.json.brokerservice.JSONEstates;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.ServiceList;
import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Account;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.services.broker.transaction.RentTransaction;
import vs.jan.services.broker.transaction.TransactionFailedException;
import vs.jan.validator.BrokerValidator;
import vs.jan.validator.Validator;

public class BrokerService {

	private Validator validator;
	private Map<Broker, JSONGameURI> brokers;
	private ServiceList services;
	private BrokerHelper helper;

	public BrokerService() {
		this.validator = new BrokerValidator();
		this.helper = new BrokerHelper(null);
		brokers = new HashMap<>();
	};

	public void createBroker(JSONGameURI game, String host) throws ResponseCodeException {
		validator.checkJsonIsValid(game, Error.JSON_GAME_URI.getMsg());
		Broker b = new Broker("/broker/" + helper.getID(game.getURI()));
		b.setGameUri(game.getURI());
		brokers.put(b, game);
		this.services = ServiceAllocator.initServices(host, helper.getID(game.getURI()));
		helper.setServices(this.services);
	}

	public JSONBrokerList getBrokers() {
		JSONBrokerList list = new JSONBrokerList();
		for (Broker b : brokers.keySet()) {
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
		String id = "/brokers/" + gameid + "/places/" + placeid;
		String visitUri = id + "/visit";
		String hypoCreditUri = id + "/hypothecarycredit";
		Place p = new Place(id, place.getPlace(), place.getValue(), place.getHouses(), visitUri, hypoCreditUri);
		broker.addPlace(p);
	}

	public ServiceList getServices() {
		return services;
	}

	public void setServices(ServiceList services) {
		this.services = services;
	}

	public synchronized JSONEventList visitPlace(String gameid, String placeid, String pawnid, String playeruri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerUriIsValid(playeruri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(brokers, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player player = helper.getPlayer(this.services.getGame() + "/" + gameid + "/players/" +  helper.getID(playeruri), gameid);
		
		
		String reason = "Player: " + player.getId() + " has visited the place: " + place.getUri();
		JSONEvent event = new JSONEvent(gameid, "visit", "visit", reason, path, playeruri);
		helper.postEvent(event, this.services.getEvents());
		
		if (place.getOwner() != null) {
			Player owner = place.getOwner();
			
			
			if (!owner.getId().equals(playeruri)) {
				RentTransaction rent = null;

				try {
					String fromUri = player.getAccount();
					validator.checkIdIsNotNull(fromUri, Error.ACC_URI.getMsg());
					JSONAccount jsonFrom = helper.getAccount(fromUri);
					JSONAccount jsonTo = helper.getAccount(owner.getAccount());
					Account from = new Account(player, jsonTo.getSaldo(), helper.getID(owner.getId()));
					Account to = new Account(owner, jsonTo.getSaldo(), helper.getID(owner.getId()));
					int amount = place.getHousesPrice() + place.getPrice();

					if (jsonFrom.getSaldo() > amount) {
						rent = new RentTransaction(from, to, amount, "rent", this.services.getBank());
						rent.execute(gameid);
						
						reason = "Player: " + player.getId() + " has payed the rent for the place: " + place.getUri();
						event = new JSONEvent(gameid, "payrent", "payrent", reason, path, playeruri);
						helper.postEvent(event, this.services.getEvents());
						
					} else {
						reason = "Player: " + player.getId() 
											+ " cannot pay the rent of: " 
												+ place.getPrice() + " for the place: " 
													+ place.getUri() + " -> Saldo: " + from.getSaldo();
						
						event = new JSONEvent(gameid, "cannotpayrent", "cannotpayrent", reason, path, playeruri);
						helper.postEvent(event, this.services.getEvents());
					}
				} catch (TransactionFailedException e) {
					place.setOwner(rent.getHistory().getTo().getPlayer());
					throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
				}
			}
		}
		
		return helper.retrieveEventList(this.services.getEvents(), playeruri, gameid, new Date());
	}

	public JSONBroker getSpecificBroker(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		Broker b = helper.getBroker(this.brokers, gameid);
		return b.convert();
	}

	public JSONPlace getSpecificPlace(String gameid, String placeid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		Broker b = helper.getBroker(this.brokers, gameid);
		Place place = helper.getPlace(b, placeid);
		return place.convert();
	}

	public Player getOwner(String gameid, String placeid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		Broker b = helper.getBroker(this.brokers, gameid);
		Place p = helper.getPlace(b, placeid);
		Player owner = new Player();

		if (p.getOwner() != null) {
			owner = p.getOwner();
		}

		return owner;

	}

	public JSONEstates getPlaces(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		Broker b = helper.getBroker(this.brokers, gameid);
		JSONEstates estates = new JSONEstates();
		b.getPlaces().forEach(p -> estates.getEstates().add(p.getUri()));
		return estates;
	}

}
