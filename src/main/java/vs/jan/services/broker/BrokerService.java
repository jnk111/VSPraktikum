package vs.jan.services.broker;

import java.util.HashMap;
import java.util.Map;

import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.brokerservice.BrokerHelper;
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
		this.helper = new BrokerHelper();
		brokers = new HashMap<>();
	};

	public void createBroker(JSONGameURI game, String host) throws ResponseCodeException {
		validator.checkJsonIsValid(game, Error.JSON_GAME_URI.getMsg());
		Broker b = new Broker("/broker/" + helper.getID(game.getURI()));
		b.setGameUri(game.getURI());
		brokers.put(b, game);
		this.services = ServiceAllocator.initServices(host, helper.getID(game.getURI()));
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
		Place p = new Place(place.getPlace());
		p.setUri("/brokers/" + gameid + "/places/" + placeid);
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

	public synchronized void visitPlace(String gameid, String placeid, String pawnid, String playeruri)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerUriIsValid(playeruri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(brokers, gameid);
		Place place = broker.getPlace(placeid);

		if (place.getOwner() != null) {
			Player owner = place.getOwner();
			Player player = helper.getPlayer(playeruri, gameid);
			
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
					} else {
						// nehme Hypothek auf? (client oder Broker?)
					}
				} catch (TransactionFailedException e) {
					place.setOwner(rent.getHistory().getTo().getPlayer());
					throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
				}
			}
		}
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
		Place place = b.getPlace(placeid);
		return place.convert();
	}

	public Player getOwner(String gameid, String placeid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		Broker b = helper.getBroker(this.brokers, gameid);
		Place p = b.getPlace(placeid);
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
