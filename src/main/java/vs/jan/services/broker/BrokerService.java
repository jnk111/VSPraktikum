package vs.jan.services.broker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.Helper;
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
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.transaction.BankSellTransaction;
import vs.jan.transaction.BuyTransaction;
import vs.jan.transaction.RentTransaction;
import vs.jan.transaction.TradeTransaction;
import vs.jan.validator.BrokerValidator;
import vs.jan.validator.Validator;

public class BrokerService {

	private final String BROKER_PREFIX = "/broker/";
	private final String PLACES_SUFFIX = "/places";
	private final String PLACES_INFIX = PLACES_SUFFIX + "/";
	private final String VISIT_SUFFIX = "/visit";
	private static final String OWNER_SUFFIX = "/owner";
	private final String HYPO_CREDIT_SUFFIX = "/hypothecarycredit";
	private final double HYPO_INTEREST = 0.10;

	private Validator validator;
	private Map<String, Broker> brokers;
	private ServiceList services;

	public BrokerService() {
		this.validator = new BrokerValidator();
		brokers = new HashMap<>();
	};

	public synchronized void createBroker(JSONGameURI game, String serviceUri) throws ResponseCodeException {
		validator.checkJsonIsValid(game, Error.JSON_GAME_URI.getMsg());

		String baseUri = BROKER_PREFIX;
		String gameid = BrokerHelper.getID(game.getURI());
		String id = baseUri + gameid;
		Broker b = new Broker(id);

		b.setGameUri(game.getURI());
		b.setEstateUri(id + PLACES_SUFFIX);

		brokers.put(gameid, b);
		this.services = ServiceAllocator.initServices(serviceUri, gameid);
		BrokerHelper.setServices(this.services);
	}

	public JSONBrokerList getBrokers() {
		JSONBrokerList list = new JSONBrokerList();
		this.brokers.forEach((k, v) -> list.getBroker().add(v.getUri()));
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
		String ownerUri = id + OWNER_SUFFIX;
		Place p = new Place(id, place.getPlace(), place.getValue(), place.getHouses(), visitUri, hypoCreditUri, ownerUri);
		broker.addPlace(p);
	}

	public synchronized JSONEventList visitPlace(String gameid, String placeid, String pawnid, String playeruri,
			String path) throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerUriIsValid(playeruri, Error.PLAYER_URI.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);
		Place place = BrokerHelper.getPlace(broker, placeid);
		Player player = BrokerHelper.getPlayer(playeruri, gameid);

		String type = EventTypes.VISIT_PLACE.getType();
		JSONEvent event = new JSONEvent(gameid, type, type, type, place.getVisitUri(), playeruri);

		BrokerHelper.postEvent(event);
		BrokerHelper.broadCastEvent(event);

		event = null;
		type = null;

		Player owner = place.getOwner();
		RentTransaction rent = null;

		try {

			if (owner != null && !owner.equals(player) && !place.isHypo() && place.isPlace()) {
				int amount = place.getRent().get(place.getHouses());
				rent = new RentTransaction(player, owner, amount, this.services.getBank(), gameid, place);
				rent.execute();
				type = EventTypes.PAY_RENT.getType();
				event = new JSONEvent(gameid, type, type, type, place.getVisitUri(), playeruri);

			}
		} catch (TransactionFailedException e) {
			rent.rollBack();
			type = EventTypes.CANNOT_PAY_RENT.getType();
			event = new JSONEvent(gameid, type, type, type, place.getVisitUri(), playeruri);

			throw new TransactionFailedException(e.getMessage());

		} finally {

			BrokerHelper.postEvent(event);
			BrokerHelper.broadCastEvent(event);
		}

		return BrokerHelper.receiveEventList(playeruri, gameid, new Date());
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

		return null;
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
		Player player = BrokerHelper.getPlayer(playerUri, gameid);

		Place place = BrokerHelper.getPlace(broker, placeid);
		Player owner = place.getOwner();
		BuyTransaction buy = null;
		JSONEvent event = null;
		String type = null;

		try {
			if (owner == null && place.isPlace()) {
				buy = new BuyTransaction(player, place.getPrice(), this.services.getBank(), gameid, place);
				buy.execute();

				type = EventTypes.BUY_PLACE.getType();
				event = new JSONEvent(gameid, type, type, type, place.getUri(), player.getId());
			}

		} catch (TransactionFailedException e) {
			buy.rollBack();

			type = EventTypes.CANNOT_BUY_PLACE.getType();
			event = new JSONEvent(gameid, type, type, type, place.getUri(), player.getId());

			throw new TransactionFailedException(e.getMessage());

		} finally {
			Helper.postEvent(event);
			Helper.broadCastEvent(event);
		}

		return BrokerHelper.receiveEventList(playerUri, gameid, new Date());
	}

	public synchronized JSONEventList takeHypothecaryCredit(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);
		Player player = BrokerHelper.getPlayer(playerUri, gameid);
		Place place = BrokerHelper.getPlace(broker, placeid);
		Player owner = place.getOwner();
		JSONEvent event = null;
		String type = null;
		BankSellTransaction sell = null;

		try {

			if (owner != null && owner.equals(player) && place.isPlace()) {

				int amountRent = (int) place.getPrice() / 2;
				int amountHouses = (int) (place.getCost().get(place.getHouses()) / 2);
				int amount = amountRent + amountHouses;

				sell = new BankSellTransaction(player, amount, this.services.getBank(), gameid, place);
				sell.execute();

				type = EventTypes.TAKE_HYPO.getType();
				event = new JSONEvent(gameid, type, type, type, place.getHypoCreditUri(), playerUri);

				broker.addHypothecaryCredit(sell);

			}

		} catch (TransactionFailedException e) {
			sell.rollBack();
			broker.removehypothecaryCredit(sell);
			throw new TransactionFailedException(e.getMessage());

		} finally {
			BrokerHelper.broadCastEvent(event);
			BrokerHelper.postEvent(event);
		}

		return BrokerHelper.receiveEventList(playerUri, gameid, new Date());
	}

	public synchronized JSONEventList deleteHypothecaryCredit(String gameid, String placeid, String playerUri,
			String path) throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = BrokerHelper.getBroker(this.brokers, gameid);
		Place place = BrokerHelper.getPlace(broker, placeid);
		Player player = BrokerHelper.getPlayer(playerUri, gameid);
		BankSellTransaction credit = broker.getHypothecaryCredit(place, BrokerHelper.getID(playerUri));
		BuyTransaction buyBack = null;
		String type = null;
		JSONEvent event = null;

		try {

			if (credit != null && place.isPlace()) {

				int amount = (int) (credit.getAmount() + (credit.getAmount() * HYPO_INTEREST));
				buyBack = new BuyTransaction(player, amount, this.services.getBank(), gameid, place);
				buyBack.execute();

				type = EventTypes.DELETE_HYPO.getType();
				event = new JSONEvent(gameid, type, type, type, place.getHypoCreditUri(), playerUri);
				broker.removehypothecaryCredit(credit);
				place.setHypo(false);
			}

		} catch (TransactionFailedException e) {
			buyBack.rollBack();
			broker.removehypothecaryCredit(credit);

			type = EventTypes.CANNOT_DELETE_HYPO.getType();
			event = new JSONEvent(gameid, type, type, type, place.getHypoCreditUri(), playerUri);
			throw new TransactionFailedException(e.getMessage());

		} finally {
			BrokerHelper.broadCastEvent(event);
			BrokerHelper.postEvent(event);
		}

		return BrokerHelper.receiveEventList(playerUri, gameid, new Date());
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
		TradeTransaction trade = null;
		String type = null;

		try {

			if (owner != null && !player.equals(owner) && place.isPlace()) {

				int amount = place.getRent().get(place.getHouses());
				trade = new TradeTransaction(player, owner, amount, this.services.getBank(), gameid, place);
				trade.execute();

				type = EventTypes.TRADE_PLACE.getType();
				event = new JSONEvent(gameid, type, type, type, place.getUri(), owner.getId());

			}
		} catch (TransactionFailedException e) {
			trade.rollBack();

			type = EventTypes.CANNOT_TRADE_PLACE.getType();
			event = new JSONEvent(gameid, type, type, type, place.getUri(), owner.getId());

			throw new TransactionFailedException(e.getMessage());

		} finally {
			BrokerHelper.broadCastEvent(event);
			BrokerHelper.postEvent(event);
		}

		return BrokerHelper.receiveEventList(owner.getId(), gameid, new Date());
	}

	public ServiceList getServices() {
		return services;
	}

	public void setServices(ServiceList services) {
		this.services = services;
	}
}
