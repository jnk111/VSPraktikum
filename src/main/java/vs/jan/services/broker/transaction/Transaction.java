package vs.jan.services.broker.transaction;

import vs.jan.model.brokerservice.Account;

public abstract class Transaction {

	protected Account from;
	protected Account to;
	protected int amount;
	protected String reason;
	protected Transaction history;

	protected String bankUri;

	protected abstract void execute(String gameid) throws TransactionFailedException;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + ((bankUri == null) ? 0 : bankUri.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((history == null) ? 0 : history.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (amount != other.amount)
			return false;
		if (bankUri == null) {
			if (other.bankUri != null)
				return false;
		} else if (!bankUri.equals(other.bankUri))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (history == null) {
			if (other.history != null)
				return false;
		} else if (!history.equals(other.history))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [from=" + from + ", to=" + to + ", amount=" + amount + ", reason=" + reason + ", history="
				+ history + ", bankUri=" + bankUri + "]";
	}

	public Account getFrom() {
		return from;
	}

	public void setFrom(Account from) {
		this.from = from;
	}

	public Account getTo() {
		return to;
	}

	public void setTo(Account to) {
		this.to = to;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Transaction getHistory() {
		return history;
	}

	public void setHistory(Transaction history) {
		this.history = history;
	}

	public String getBankUri() {
		return bankUri;
	}

	public void setBankUri(String bankUri) {
		this.bankUri = bankUri;
	}

}
