package vs.jan.transaction;

import vs.jan.json.brokerservice.JSONAccount;
import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.TransactionFailedException;

public abstract class Transaction {

	protected Player from;
	protected Player to;
	protected int amount;
	protected Transaction history;
	protected String gameId;
	protected String bankUri;
	
	protected Place place;
	protected JSONAccount fromAcc;
	protected JSONAccount toAcc;

	public abstract void execute() throws TransactionFailedException;
	public abstract void rollBack();

	public Player getFrom() {
		return from;
	}

	public void setFrom(Player from) {
		this.from = from;
	}

	public Player getTo() {
		return to;
	}

	public void setTo(Player to) {
		this.to = to;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Transaction getHistory() {
		return history;
	}

	public void setHistory(Transaction history) {
		this.history = history;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getBankUri() {
		return bankUri;
	}

	public void setBankUri(String bankUri) {
		this.bankUri = bankUri;
	}
	
	public Place getPlace() {
		return place;
	}
	
	public void setPlace(Place place) {
		this.place = place;
	}
	
	public JSONAccount getFromAcc() {
		return fromAcc;
	}
	
	public void setFromAcc(JSONAccount fromAcc) {
		this.fromAcc = fromAcc;
	}
	
	public JSONAccount getToAcc() {
		return toAcc;
	}
	
	public void setToAcc(JSONAccount toAcc) {
		this.toAcc = toAcc;
	}
	
	
}
