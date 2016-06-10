package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.tools.HttpService;

public class BankSellTransaction extends Transaction{

	private Place place;
	
	public BankSellTransaction(String to, int amount, Place place, String bankUri, String gameId) {
		
		this.to = to;
		this.amount = amount;
		this.place = place;
		this.bankUri = bankUri;
		this.gameId = gameId;
	}
	
	public BankSellTransaction(BankSellTransaction trans) {
		
		this.to = trans.getTo();
		this.amount = trans.getAmount();
		this.place = new Place(trans.getPlace());
		this.bankUri = trans.getBankUri();
		this.gameId = trans.getGameId();
	}
	
	
	@Override
	public void execute() throws TransactionFailedException {
		this.history = new BankSellTransaction(this);
		
		try {
			String url = this.bankUri + "/" + this.gameId + "/transfer/to/" + this.to + "/" + this.amount;
			
			HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);

		} catch (Exception e) {
			throw new TransactionFailedException();
		}
	}

	public Place getPlace() {
		return place;
	}


	public void setPlace(Place place) {
		this.place = place;
	}
	
	

}
