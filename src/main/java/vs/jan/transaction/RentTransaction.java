package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Account;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.tools.HttpService;

public class RentTransaction extends Transaction {

	public RentTransaction(Account from, Account to, int amount) {

		this(from, to, amount, null, null);
	}

	public RentTransaction(Account from, Account to, int amount, String reason, String bankUri) {
		super();
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.bankUri = bankUri;
		this.reason = reason;
	}

	@Override
	public void execute(String gameid) throws TransactionFailedException {

		this.history = copy();

		try {
			String idFrom = getID(this.from.getAccUri());
			String idTo = getID(this.to.getAccUri());
			this.to.setSaldo(this.to.getSaldo() + this.amount);
			this.from.setSaldo(this.from.getSaldo() - this.amount);
			String url = this.bankUri + "/" + gameid + "/transfer/from/" + idFrom + "/to/" + idTo + "/" + amount
					+ "?transaction=/transactions/" + gameid;
			
			
			HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);

		} catch (Exception e) {
			throw new TransactionFailedException();
		}
	}

	private RentTransaction copy() {

		Player oldFrom = this.from.getPlayer();
		Player oldTo = this.to.getPlayer();

		Player newFrom = new Player(oldFrom.getUserName(), oldFrom.getId(), oldFrom.getPawn(), oldFrom.getAccount(),
				oldFrom.isReady());

		Player newTo = new Player(oldTo.getUserName(), oldTo.getId(), oldTo.getPawn(), oldTo.getAccount(), oldTo.isReady());

		Account from = new Account(newFrom, this.from.getSaldo(), this.from.getAccUri());

		Account to = new Account(newTo, this.to.getSaldo(), this.to.getAccUri());

		RentTransaction copy = new RentTransaction(from, to, amount, this.reason, this.bankUri);

		return copy;
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

	public void setHistory(RentTransaction history) {
		this.history = history;
	}

	public String getBank() {
		return bankUri;
	}

	public void setBank(String bank) {
		this.bankUri = bank;
	}



}
