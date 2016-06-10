package vs.jan.transaction;

import vs.jan.model.exception.TransactionFailedException;

public abstract class Transaction {

	protected String from;
	protected String to;
	protected int amount;
	protected Transaction history;
	protected String gameId;

	protected String bankUri;

	public abstract void execute() throws TransactionFailedException;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
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
}
