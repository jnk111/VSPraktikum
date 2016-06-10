package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.model.exception.TransactionFailedException;
import vs.jan.tools.HttpService;

public class BuyTransaction extends Transaction{

	
	public BuyTransaction(String from, int amount, String bankUri, String gameId) {
		this.from = from;
		this.amount = amount;
		this.history = null;
		this.bankUri = bankUri;
		this.gameId = gameId;
	}
	
	public BuyTransaction(BuyTransaction trans) {
		this.from = trans.getFrom();
		this.amount = trans.getAmount();
		this.bankUri = trans.getBankUri();
		this.gameId = trans.getGameId();
		
	}

	@Override
	public void execute() throws TransactionFailedException {
		this.history = new BuyTransaction(this);
		
		try {
			
			String url = this.bankUri + "/" + this.gameId + "/transfer/from/" + this.from + "/" + this.amount;
			HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);

		} catch (Exception e) {
			throw new TransactionFailedException();
		}
	}
}
