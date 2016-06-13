package vs.jan.services.broker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.model.exception.PlaceNotHasAnOwnerException;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.model.exception.TransactionRollBackException;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.transaction.BuyTransaction;
import vs.jan.transaction.TradeTransaction;
import vs.jan.transaction.BankSellTransaction;
import vs.jan.validator.BrokerValidator;
import vs.jan.validator.Validator;

public class BrokerService {

	private Validator validator;
	private Map<Broker, JSONGameURI> brokers;
	private ServiceList services;
	private BrokerHelper helper;
	private List<String> users;

	public BrokerService() {
		this.validator = new BrokerValidator();
		this.helper = new BrokerHelper(null);
		brokers = new HashMap<>();
		setUsers(new ArrayList<>());
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


	public synchronized JSONEventList visitPlace(String gameid, String placeid, String pawnid, String playeruri,
			String path) throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerUriIsValid(playeruri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(brokers, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player player = helper.getPlayer(this.services.getGamesHost() + playeruri, gameid);

		String reason = "Player: " + player.getId() + " has visited the place: " + place.getUri();
		JSONEvent event = new JSONEvent(gameid, EventTypes.VISIT_PLACE.getType(), EventTypes.VISIT_PLACE.getType(), reason,
				path, playeruri);
		
		helper.postEvent(event, this.services.getEvents());
		helper.broadCastEvent(event, this.services.getUsers());
		
		Player owner = place.getOwner();
		JSONAccount from = null;
		JSONAccount to = null;
		TradeTransaction rent = null;

		if (owner != null && !owner.equals(player) && !place.isHypo() && place.isPlace()) {

			try {
				from = helper.getAccount(player.getAccount());
				
				to = helper
						.getAccount(owner.getAccount());
				int amount = place.getRent().get(place.getLevel());

				if (from.getSaldo() >= amount) {
					rent = new TradeTransaction(helper.getID(from.getPlayer()), helper.getID(to.getPlayer()), amount,
							this.services.getBank(), gameid);
					rent.execute();

					reason = "Player: " + player.getId() + " has payed the rent for the place: " + place.getUri();
					event = new JSONEvent(gameid, EventTypes.PAY_RENT.getType(), EventTypes.PAY_RENT.getType(), reason, path,
							playeruri);

				} else {
					reason = "Player: " + player.getId() + " cannot pay the rent of: " + place.getPrice() + " for the place: "
							+ place.getUri() + " -> Saldo: " + from.getSaldo();

					event = new JSONEvent(gameid, EventTypes.CANNOT_PAY_RENT.getType(), EventTypes.CANNOT_PAY_RENT.getType(),
							reason, path, playeruri);
				}
			} catch (Exception e) {
				
				JSONAccount f = helper
						.getAccount(player.getAccount());
				JSONAccount t = helper
						.getAccount(owner.getAccount());

				if (f.getSaldo() != from.getSaldo() || t.getSaldo() != to.getSaldo()) {

					throw new TransactionRollBackException(Error.ROLL_BACK_FAILED.getMsg());
				} else {
					throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
				}
			}
		}

		if (event != null) {
			helper.postEvent(event, this.services.getEvents());
			helper.broadCastEvent(event, this.services.getUsers());
		}

		return helper.receiveEventList(this.services.getEvents(), playeruri, gameid, new Date());
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
		Player player = helper.getPlayer(this.services.getGamesHost() + playerUri, gameid);

		JSONAccount from = helper
				.getAccount(player.getAccount());
		Place place = helper.getPlace(broker, placeid);
		BuyTransaction buy = null;
		String reason = "Player: " + player.getId() + " wants to buy the place: " + place.getUri();
		JSONEvent event = null;

		if (place.getOwner() == null && place.isPlace()) {
			if (from.getSaldo() >= place.getPrice()) {

				try {

					buy = new BuyTransaction(helper.getID(from.getPlayer()), place.getPrice(), this.services.getBank(), gameid);
					buy.execute();
					place.setOwner(player);
					event = new JSONEvent(gameid, EventTypes.BUY_PLACE.getType(), EventTypes.BUY_PLACE.getType(), reason, path,
							playerUri);
				} catch (Exception e) {

					JSONAccount f = helper
							.getAccount(player.getAccount());

					if (f.getSaldo() == from.getSaldo()) {
						throw new TransactionRollBackException(Error.ROLL_BACK_FAILED.getMsg());
					} else {

						place.setOwner(null);
						throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
					}
				}
			} else {

				event = new JSONEvent(gameid, EventTypes.CANNOT_BUY_PLACE.getType(), EventTypes.CANNOT_BUY_PLACE.getType(),
						reason, path, playerUri);
			}
		}

		if (event != null) {
			helper.broadCastEvent(event, this.services.getUsers());
			helper.postEvent(event, this.services.getEvents());
		}

		return helper.receiveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public JSONEventList takeHypothecaryCredit(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(this.brokers, gameid);

		Player player = helper.getPlayer(this.services.getGamesHost() + playerUri, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player owner = place.getOwner();
		JSONEvent event = null;

		if (owner != null && owner.equals(player) && place.isPlace()) {

			JSONAccount to = helper
					.getAccount(player.getAccount());

			BankSellTransaction sell = null;
			int amountRent = (int) place.getPrice() / 2;
			int amountHouses = (int) (place.getHousesPrice() * place.getLevel()) / 2;
			int amount = amountRent + amountHouses;

			try {
				sell = new BankSellTransaction(helper.getID(to.getPlayer()), amount, place, this.services.getBank(), gameid);
				sell.execute();
				place.setHypo(true);
				String reason = "Player: " + player.getId() + " has taken a hypothecary credit on: " + place.getUri();
				event = new JSONEvent(gameid, EventTypes.TAKE_HYPO.getType(), EventTypes.TAKE_HYPO.getType(), reason, path,
						playerUri);
				broker.addHypothecaryCredit(sell);

			} catch (Exception e) {

				JSONAccount t = helper
						.getAccount(this.services.getBank() + "/" + gameid + "/accounts" + "/" + helper.getID(playerUri));

				if (to.getSaldo() == t.getSaldo()) {
					throw new TransactionRollBackException(Error.ROLL_BACK_FAILED.getMsg());
				} else {
					place.setHypo(false);
					broker.removehypothecaryCredit(sell);
					throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
				}
			}
		}

		if (event != null) {
			helper.broadCastEvent(event, this.services.getUsers());
			helper.postEvent(event, this.services.getEvents());
		}

		return helper.receiveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public JSONEventList deleteHypothecaryCredit(String gameid, String placeid, String playerUri, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkPlayerUriIsValid(playerUri, Error.PLAYER_URI.getMsg());

		Broker broker = helper.getBroker(this.brokers, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player player = helper.getPlayer(this.services.getGamesHost() + playerUri, gameid);
		BankSellTransaction credit = broker.getHypothecaryCredit(place, helper.getID(playerUri));
		BuyTransaction buyBack = null;
		JSONEvent event = null;

		if (credit != null && place.isPlace()) {

			int amount = (int) (credit.getAmount() + (credit.getAmount() * 0.10));

			JSONAccount from = helper
					.getAccount(player.getAccount());
			String reason = "Player: " + player.getId() + " want to delete his hypothecary credit for: " + place.getUri();

			if (from.getSaldo() >= amount) {
				
				try {
					buyBack = new BuyTransaction(helper.getID(from.getPlayer()), amount, this.services.getBank(), gameid);
					buyBack.execute();

					event = new JSONEvent(gameid, EventTypes.DELETE_HYPO.getType(), EventTypes.DELETE_HYPO.getType(), reason,
							path, playerUri);
					broker.removehypothecaryCredit(credit);
					place.setHypo(false);

				} catch (Exception e) {

					JSONAccount f = helper
							.getAccount(player.getAccount());

					if (from.getSaldo() == f.getSaldo()) {
						throw new TransactionRollBackException(Error.ROLL_BACK_FAILED.getMsg());
					} else {
						place.setHypo(true);
						broker.addHypothecaryCredit(credit);
						throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
					}
				}

			} else {
				event = new JSONEvent(gameid, EventTypes.CANNOT_DELETE_HYPO.getType(), EventTypes.CANNOT_DELETE_HYPO.getType(),
						reason, path, playerUri);
			}
		}

		if (event != null) {
			helper.broadCastEvent(event, this.services.getUsers());
			helper.postEvent(event, this.services.getEvents());
		}

		return helper.receiveEventList(this.services.getEvents(), playerUri, gameid, new Date());
	}

	public JSONEventList tradePlace(String gameid, String placeid, Player player, String path)
			throws TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
		validator.checkJsonIsValid(player, Error.JSON_PLAYER.getMsg());

		Broker broker = helper.getBroker(this.brokers, gameid);
		Place place = helper.getPlace(broker, placeid);
		Player owner = place.getOwner();
		JSONAccount from = null;
		JSONAccount to = null;
		JSONEvent event = null;
		String reason = null;

		if (owner != null && !player.equals(owner) && place.isPlace()) {

			from = helper
					.getAccount(player.getAccount());
			to = helper.getAccount(owner.getAccount());
			int amount = place.getRent().get(place.getLevel());

			if (to.getSaldo() >= amount) {

				try {
					TradeTransaction trade = new TradeTransaction(helper.getID(from.getPlayer()), helper.getID(to.getPlayer()),
							amount, this.services.getBank(), gameid);
					trade.execute();
					place.setOwner(player);
					reason = "Owner: " + owner.getId() + " trades the place: " + place.getUri() + " to the player: "
							+ player.getId();
					event = new JSONEvent(gameid, EventTypes.TRADE_PLACE.getType(), EventTypes.TRADE_PLACE.getType(), reason,
							path, owner.getId());

				} catch (Exception e) {

					JSONAccount f = helper
							.getAccount(this.services.getBank() + "/" + gameid + "/accounts" + "/" + helper.getID(player.getId()));
					JSONAccount t = helper
							.getAccount(this.services.getBank() + "/" + gameid + "/accounts" + "/" + helper.getID(owner.getId()));

					if (from.getSaldo() == f.getSaldo() || to.getSaldo() == t.getSaldo()) {
						throw new TransactionRollBackException(Error.ROLL_BACK_FAILED.getMsg());

					} else {
						place.setOwner(owner);
						throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
					}
				}
			} else {

				reason = "Owner: " + owner.getId() + " cannot trade the place: " + place.getUri() + " to the player: "
						+ player.getId() + " -> Saldo: " + to.getSaldo() + ", amount: " + amount;

				event = new JSONEvent(gameid, EventTypes.CANNOT_TRADE_PLACE.getType(), EventTypes.TRADE_PLACE.getType(), reason,
						path, owner.getId());
			}
		}

		if (event != null) {

			helper.broadCastEvent(event, this.services.getUsers());
			helper.postEvent(event, this.services.getEvents());
		}

		return helper.receiveEventList(this.services.getEvents(), owner.getId(), gameid, new Date());
	}
	
	public ServiceList getServices() {
		return services;
	}

	public void setServices(ServiceList services) {
		this.services = services;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
}
