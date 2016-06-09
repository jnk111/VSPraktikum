package vs.jan.services.broker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.brokerservice.BrokerHelper;
import vs.jan.helper.events.EventTypes;
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
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.transaction.BuyTransaction;
import vs.jan.transaction.RentTransaction;
import vs.jan.transaction.SellTransaction;
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
		String baseUri = "/broker/";
		String gameid = helper.getID(game.getURI());
		String id = baseUri + gameid;
		Broker b = new Broker(id);
		b.setGameUri(game.getURI());
		b.setEstateUri(id + "/places");
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

	public synchronized JSONEventList visitPlace(String gameid, String placeid, String pawnid, String playeruri,
			String path) throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerUriIsValid(playeruri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(brokers, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player player = helper.getPlayer(this.services.getGame() + "/" + gameid + "/players/" + helper.getID(playeruri),
				gameid);

		String reason = "Player: " + player.getId() + " has visited the place: " + place.getUri();
		JSONEvent event = new JSONEvent(gameid, EventTypes.VISIT_PLACE.getType(), EventTypes.VISIT_PLACE.getType(), reason, path, playeruri);
		helper.postEvent(event, this.services.getEvents());
		Player owner = place.getOwner();
		RentTransaction rent = null;

		if (owner != null && !owner.getId().equals(playeruri)) {

			try {
				String fromUri = player.getAccount();
				validator.checkIdIsNotNull(fromUri, Error.ACC_URI.getMsg());
				JSONAccount jsonFrom = helper.getAccount(fromUri);
				JSONAccount jsonTo = helper.getAccount(owner.getAccount());
				Account from = new Account(player, jsonTo.getSaldo(), helper.getID(owner.getId()));
				Account to = new Account(owner, jsonTo.getSaldo(), helper.getID(owner.getId()));
				int amount = place.getRent().get(place.getLevel());

				if (jsonFrom.getSaldo() > amount) {
					rent = new RentTransaction(from, to, amount, "rent", this.services.getBank());
					rent.execute(gameid);

					reason = "Player: " + player.getId() + " has payed the rent for the place: " + place.getUri();
					event = new JSONEvent(gameid, EventTypes.PAY_RENT.getType(), EventTypes.PAY_RENT.getType(), reason, path, playeruri);
					helper.postEvent(event, this.services.getEvents());

				} else {
					reason = "Player: " + player.getId() + " cannot pay the rent of: " + place.getPrice() + " for the place: "
							+ place.getUri() + " -> Saldo: " + from.getSaldo();

					event = new JSONEvent(gameid, EventTypes.CANNOT_PAY_RENT.getType(), EventTypes.CANNOT_PAY_RENT.getType(), reason, path, playeruri);
					helper.postEvent(event, this.services.getEvents());
				}
			} catch (Exception e) {
				place.setOwner(rent.getHistory().getTo().getPlayer());
				throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
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
		Player owner = null;

		if (p.getOwner() != null) {
			return p.getOwner();
		}
		
		throw new PlaceNotHasAnOwnerException(Error.NO_OWNER.getMsg());

	}

	public JSONEstates getPlaces(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		Broker b = helper.getBroker(this.brokers, gameid);
		JSONEstates estates = new JSONEstates();
		b.getPlaces().forEach(p -> estates.getEstates().add(p.getUri()));
		return estates;
	}

	public JSONEventList buyPlace(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(this.brokers, gameid);
		Player player = helper.getPlayer(this.services.getGame() + playerUri.replace("/games", ""), gameid);
		
		// Temp
		JSONAccount jsonFrom = helper.getAccount(this.services.getBank() + "/" + gameid + "/accounts" + "/" + helper.getID(playerUri));
		
//		JSONAccount jsonFrom = helper.getAccount(player.getAccount());
		Account from = new Account(player, jsonFrom.getSaldo(), helper.getID(player.getId()));
		Place place = helper.getPlace(broker, placeid);
		BuyTransaction buy = null;
		String reason = "Player: " + player.getId() + " wants to buy the place: " + place.getUri();
		JSONEvent event = null;
		
		if (place.getOwner() == null) {
			if (from.getSaldo() > place.getPrice()) {

				try {
					
					buy = new BuyTransaction(from, place.getPrice(), this.services.getBank());
					buy.execute(gameid);
					place.setOwner(player);
					event = new JSONEvent(gameid, EventTypes.BUY_PLACE.getType(), EventTypes.BUY_PLACE.getType(), reason, path, playerUri);
				} catch (Exception e) {
					place.setOwner(null);
					throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
				}
			} else {
				
				event = new JSONEvent(gameid, EventTypes.CANNOT_BUY_PLACE.getType(), EventTypes.CANNOT_BUY_PLACE.getType(), reason, path, playerUri);
			}
		}
		
		helper.postEvent(event, this.services.getEvents());
		return helper.retrieveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public JSONEventList takeHypothecaryCredit(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(this.brokers, gameid);
		Player player = helper.getPlayer(playerUri, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player owner = place.getOwner();

		if (owner != null && owner.getId().equals(playerUri)) {
			JSONAccount jsonFrom = helper.getAccount(player.getAccount());
			Account to = new Account(player, jsonFrom.getSaldo(), helper.getID(player.getId()));

			SellTransaction sell = null;
			int amountRent = (int) place.getPrice() / 2;
			int amountHouses = (int) (place.getHousesPrice() * place.getLevel()) / 2;
			int amount = amountRent + amountHouses;

			try {
				sell = new SellTransaction(to, amount, place, this.services.getBank());
				sell.execute(gameid);
				place.setOwner(null);
				String reason = "Player: " + player.getId() + " has taken a hypothecary credit on: " + place.getUri();
				JSONEvent event = new JSONEvent(gameid, EventTypes.TAKE_HYPO.getType(), EventTypes.TAKE_HYPO.getType(), reason, path, playerUri);
				broker.addHypothecaryCredit(sell);
				helper.postEvent(event, this.services.getEvents());

			} catch (Exception e) {

				place.setOwner(sell.getHistory().getTo().getPlayer());
				broker.removehypothecaryCredit(sell);
				throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
			}
		}

		return helper.retrieveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public JSONEventList deleteHypothecaryCredit(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(this.brokers, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player player = helper.getPlayer(playerUri, gameid);
		SellTransaction credit = broker.getHypothecaryCredit(placeid, playerUri);
		BuyTransaction buyBack = null;

		if (credit != null) {

			int amount = (int) (credit.getAmount() + (credit.getAmount() * 0.10));
			JSONAccount jsonFrom = helper.getAccount(player.getAccount());
			Account from = new Account(player, jsonFrom.getSaldo(), helper.getID(player.getId()));
			JSONEvent event = null;
			String reason = "Player: " + player.getId() + " want to delete his hypothecary credit for: " + place.getUri();

			if (from.getSaldo() >= amount) {
				try {
					buyBack = new BuyTransaction(from, amount, this.services.getBank());
					buyBack.execute(gameid);

					event = new JSONEvent(gameid, EventTypes.DELETE_HYPO.getType(), EventTypes.DELETE_HYPO.getType(), reason, path, playerUri);
					helper.postEvent(event, this.services.getEvents());
					place.setOwner(player);
					
				} catch (Exception e) {
					place.setOwner(null);
					throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
				}
				
			} else {
				event = new JSONEvent(gameid, EventTypes.CANNOT_DELETE_HYPO.getType(), "cannotdeletehypothecary", reason, path, playerUri);
			}
		}

		return helper.retrieveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}
}
