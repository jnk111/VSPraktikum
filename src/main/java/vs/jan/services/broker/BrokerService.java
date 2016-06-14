package vs.jan.services.broker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.brokerservice.BrokerHelper;
import vs.jan.helper.events.EventTypes;
import vs.jan.json.boardservice.JSONEvent;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.json.brokerservice.JSONBrokerList;
import vs.jan.json.brokerservice.JSONEstates;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.ServiceList;
import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.model.exception.PlaceNotHasAnOwnerException;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.transaction.BuyTransaction;
import vs.jan.transaction.RentTransaction;
import vs.jan.transaction.TradeTransaction;
import vs.jan.transaction.BankSellTransaction;
import vs.jan.validator.BrokerValidator;
import vs.jan.validator.Validator;

public class BrokerService {

	private final String BROKER_PREFIX = "/broker/";
	private final String PLACES_SUFFIX = "/places";
	private final String PLACES_INFIX = PLACES_SUFFIX + "/";
	private final String VISIT_SUFFIX = "/visit";
	private final String HYPO_CREDIT_SUFFIX = "/hypothecarycredit";
	private final double HYPO_INTEREST = 0.10;

	private Validator validator;
	private Map<Broker, JSONGameURI> brokers;
	private ServiceList services;

	public BrokerService() {
		this.validator = new BrokerValidator();
		brokers = new HashMap<>();
	};

	public synchronized void createBroker(JSONGameURI game, String host) throws ResponseCodeException {
		validator.checkJsonIsValid(game, Error.JSON_GAME_URI.getMsg());

		String baseUri = BROKER_PREFIX;
		String gameid = BrokerHelper.getID(game.getURI());
		String id = baseUri + gameid;
		Broker b = new Broker(id);

		b.setGameUri(game.getURI());
		b.setEstateUri(id + PLACES_SUFFIX);

		brokers.put(b, game);
		this.services = ServiceAllocator.initServices(host, BrokerHelper.getID(game.getURI()));
	}

	public JSONBrokerList getBrokers() {
		JSONBrokerList list = new JSONBrokerList();

		for (Broker b : brokers.keySet()) {
			list.getBroker().add(b.getUri());
		}

		return list;
	}

	public synchronized void placeBroker(String gameid, JSONBroker broker) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkJsonIsValid(broker, Error.JSON_Broker.getMsg());

		Broker b = BrokerHelper.getBroker(this.brokers, gameid);
		b.setName(broker.getName());
	}

	public synchronized void registerPlace(String gameid, String placeid, JSONPlace place) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkJsonIsValid(place, Error.JSON_PLACE.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);
		String id = BROKER_PREFIX + gameid + PLACES_INFIX + placeid;
		String visitUri = id + VISIT_SUFFIX;
		String hypoCreditUri = id + HYPO_CREDIT_SUFFIX;
		Place p = new Place(id, place.getPlace(), place.getValue(), place.getHouses(), visitUri, hypoCreditUri);
		broker.addPlace(p);
	}

	public synchronized JSONEventList visitPlace(String gameid, String placeid, String pawnid, String playeruri,
			String path) throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerUriIsValid(playeruri, Error.PLAYER_URI.getMsg());

		Broker broker = BrokerHelper.getBroker(brokers, gameid);
		Place place = BrokerHelper.getPlace(broker, placeid);
		Player player = BrokerHelper.getPlayer(this.services.getGamesHost() + playeruri, gameid);

		String reason = "Player: " + player.getId() + " has visited the place: " + place.getUri();
		JSONEvent event = new JSONEvent(gameid, EventTypes.VISIT_PLACE.getType(), EventTypes.VISIT_PLACE.getType(), reason,
				path, playeruri);

		BrokerHelper.postEvent(event, this.services.getEvents());
		BrokerHelper.broadCastEvent(event, this.services.getUsers());

		Player owner = place.getOwner();
		RentTransaction rent = null;

		if (owner != null && !owner.equals(player) && !place.isHypo() && place.isPlace()) {

			try {

				int amount = place.getRent().get(place.getLevel());

				rent = new RentTransaction(player, owner, amount, this.services.getBank(), gameid, place);
				rent.execute();

				reason = "Player: " + player.getId() + " has payed the rent for the place: " + place.getUri();
				event = new JSONEvent(gameid, EventTypes.PAY_RENT.getType(), EventTypes.PAY_RENT.getType(), reason, path,
						playeruri);

			} catch (TransactionFailedException e) {
				rent.rollBack();
				throw new TransactionFailedException(e.getMessage());
			}
		}

		BrokerHelper.postEvent(event, this.services.getEvents());
		BrokerHelper.broadCastEvent(event, this.services.getUsers());
		return BrokerHelper.receiveEventList(this.services.getEvents(), playeruri, gameid, new Date());
	}

	public JSONBroker getSpecificBroker(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());

		Broker b = BrokerHelper.getBroker(this.brokers, gameid);
		return b.convert();
	}

	public JSONPlace getSpecificPlace(String gameid, String placeid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());

		Broker b = BrokerHelper.getBroker(this.brokers, gameid);
		Place place = BrokerHelper.getPlace(b, placeid);

		return place.convert();
	}

	public Player getOwner(String gameid, String placeid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());

		Broker b = BrokerHelper.getBroker(this.brokers, gameid);
		Place p = BrokerHelper.getPlace(b, placeid);

		if (p.getOwner() != null) {
			return p.getOwner();
		}

		throw new PlaceNotHasAnOwnerException(Error.NO_OWNER.getMsg());
	}

	public JSONEstates getPlaces(String gameid) {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());

		Broker b = BrokerHelper.getBroker(this.brokers, gameid);
		JSONEstates estates = new JSONEstates();

		b.getPlaces().forEach(p -> estates.getEstates().add(p.getUri()));
		return estates;
	}

	public synchronized JSONEventList buyPlace(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);
		Player player = BrokerHelper.getPlayer(this.services.getGamesHost() + playerUri, gameid);

		Place place = BrokerHelper.getPlace(broker, placeid);
		BuyTransaction buy = null;
		String reason = null;
		JSONEvent event = null;

		try {

			buy = new BuyTransaction(player, place.getPrice(), this.services.getBank(), gameid, place);
			buy.execute();
			place.setOwner(player);
			reason = "Player: " + player.getId() + " wants to buy the place: " + place.getUri();
			event = new JSONEvent(gameid, EventTypes.BUY_PLACE.getType(), EventTypes.BUY_PLACE.getType(), reason, path,
					playerUri);

		} catch (TransactionFailedException e) {

			buy.rollBack();
			throw new TransactionFailedException(e.getMessage());
		}

		BrokerHelper.broadCastEvent(event, this.services.getUsers());
		BrokerHelper.postEvent(event, this.services.getEvents());

		return BrokerHelper.receiveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public synchronized JSONEventList takeHypothecaryCredit(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);

		Player player = BrokerHelper.getPlayer(this.services.getGamesHost() + playerUri, gameid);
		Place place = BrokerHelper.getPlace(broker, placeid);
		Player owner = place.getOwner();
		JSONEvent event = null;
		String reason = null;
		BankSellTransaction sell = null;

		if (owner != null && owner.equals(player) && place.isPlace()) {

			int amountRent = (int) place.getPrice() / 2;
			int amountHouses = (int) (place.getHousesPrice() * place.getLevel()) / 2;
			int amount = amountRent + amountHouses;

			try {
				sell = new BankSellTransaction(player, amount, this.services.getBank(), gameid, place);
				sell.execute();

				reason = "Player: " + player.getId() + " has taken a hypothecary credit on: " + place.getUri();
				event = new JSONEvent(gameid, EventTypes.TAKE_HYPO.getType(), EventTypes.TAKE_HYPO.getType(), reason, path,
						playerUri);
				broker.addHypothecaryCredit(sell);

			} catch (TransactionFailedException e) {
				sell.rollBack();
				broker.removehypothecaryCredit(sell);
				throw new TransactionFailedException(e.getMessage());
			}
		}

		BrokerHelper.broadCastEvent(event, this.services.getUsers());
		BrokerHelper.postEvent(event, this.services.getEvents());

		return BrokerHelper.receiveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public synchronized JSONEventList deleteHypothecaryCredit(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);
		Place place = BrokerHelper.getPlace(broker, placeid);
		Player player = BrokerHelper.getPlayer(this.services.getGamesHost() + playerUri, gameid);
		BankSellTransaction credit = broker.getHypothecaryCredit(place, BrokerHelper.getID(playerUri));
		BuyTransaction buyBack = null;
		JSONEvent event = null;
		String reason = null;

		if (credit != null && place.isPlace()) {

			int amount = (int) (credit.getAmount() + (credit.getAmount() * HYPO_INTEREST));

			reason = "Player: " + player.getId() + " want to delete his hypothecary credit for: " + place.getUri();

			try {
				buyBack = new BuyTransaction(player, amount, this.services.getBank(), gameid, place);
				buyBack.execute();

				event = new JSONEvent(gameid, EventTypes.DELETE_HYPO.getType(), EventTypes.DELETE_HYPO.getType(), reason, path,
						playerUri);
				broker.removehypothecaryCredit(credit);
				place.setHypo(false);

			} catch (TransactionFailedException e) {
				broker.removehypothecaryCredit(credit);
				buyBack.rollBack();
				throw new TransactionFailedException(e.getMessage());
			}
		}

		BrokerHelper.broadCastEvent(event, this.services.getUsers());
		BrokerHelper.postEvent(event, this.services.getEvents());

		return BrokerHelper.receiveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public synchronized JSONEventList tradePlace(String gameid, String placeid, Player player, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkJsonIsValid(player, Error.JSON_PLAYER.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);
		Place place = BrokerHelper.getPlace(broker, placeid);
		Player owner = place.getOwner();
		JSONEvent event = null;
		String reason = null;
		TradeTransaction trade = null;

		if (owner != null && !player.equals(owner) && place.isPlace()) {

			int amount = place.getRent().get(place.getLevel());

			try {
				trade = new TradeTransaction(player, owner, amount, this.services.getBank(), gameid, place);
				trade.execute();
				reason = "Owner: " + owner.getId() + " trades the place: " + place.getUri() + " to the player: "
						+ player.getId();
				event = new JSONEvent(gameid, EventTypes.TRADE_PLACE.getType(), EventTypes.TRADE_PLACE.getType(), reason, path,
						owner.getId());

			} catch (TransactionFailedException e) {
				trade.rollBack();
				throw new TransactionFailedException(e.getMessage());
			}
		}

		BrokerHelper.broadCastEvent(event, this.services.getUsers());
		BrokerHelper.postEvent(event, this.services.getEvents());

		return BrokerHelper.receiveEventList(this.services.getEvents(), owner.getId(), gameid, new Date());
	}

	public ServiceList getServices() {
		return services;
	}

	public void setServices(ServiceList services) {
		this.services = services;
	}
}
