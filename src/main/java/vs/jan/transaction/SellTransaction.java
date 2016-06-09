package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Account;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.tools.HttpService;

public class SellTransaction extends Transaction{

	private Place place;
	public SellTransaction(Account to, int amount, Place place) {
		
		this.to = to;
		this.amount = amount;
		this.history = null;
		this.place = place;
	}
	
	
	@Override
	public void execute(String gameid) throws TransactionFailedException {
		this.history = copy();
		
		try {
			
			String idTo = getID(this.to.getAccUri());
			this.to.setSaldo(this.to.getSaldo() + this.amount);
			String url = this.bankUri + "/" + gameid + "/transfer/to/" + idTo + "/" + this.amount;
			
			HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);

		} catch (Exception e) {
			throw new TransactionFailedException();
		}
		
	}


	private SellTransaction copy() {
		Player oldTo = this.to.getPlayer();
		Player newTo = new Player(oldTo.getUserName(), oldTo.getId(), oldTo.getPawn(), oldTo.getAccount(),
				oldTo.isReady());

		Account to = new Account(newTo, this.to.getSaldo(), this.to.getAccUri());
		Place newPlace = new Place(this.place.getUri(), this.place.getPlaceUri(), this.place.getPrice(), this.place.getHousesPrice(), this.place.getVisitUri(), this.place.getHypoCreditUri());
		SellTransaction copy = new SellTransaction(to, this.amount, newPlace);

		return copy;
	}


	public Place getPlace() {
		return place;
	}


	public void setPlace(Place place) {
		this.place = place;
	}
	
	

}
