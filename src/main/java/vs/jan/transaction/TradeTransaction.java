package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.model.exception.TransactionFailedException;
import vs.jan.tools.HttpService;

public class TradeTransaction extends Transaction {


	public TradeTransaction(String from, String to, int amount, String bankUri, String gameId) {
		super();
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.bankUri = bankUri;
		this.gameId = gameId;
		this.history = new TradeTransaction(this);
	}

	public TradeTransaction(TradeTransaction trans) {
		super();
		this.from = trans.getFrom();
		this.to = trans.getTo();
		this.amount = trans.getAmount();
		this.bankUri = trans.getBankUri();
		this.gameId = trans.getGameId();
	}

	@Override
	public void execute() throws TransactionFailedException {

		try {

			String url = this.bankUri + "/" + this.gameId + "/transfer/from/" + this.from + "/to/" + this.to + "/" + this.amount;
			HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);

		} catch (Exception e) {
			throw new TransactionFailedException();
		}
	}
}
