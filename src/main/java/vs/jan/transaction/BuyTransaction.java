package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Account;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.tools.HttpService;

public class BuyTransaction extends Transaction{

	
	public BuyTransaction(Account from, int amount) {
		this.from = from;
		this.amount = amount;
		this.history = null;
	}

	@Override
	public void execute(String gameid) throws TransactionFailedException {
		this.history = copy();
		
		try {
			String idFrom = getID(this.from.getAccUri());
			this.from.setSaldo(this.from.getSaldo() - this.amount);
			String url = this.bankUri + "/" + gameid + "/transfer/from/" + idFrom + "/" + this.amount 
					+ "?transaction=/transactions/" + gameid;
			
			HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);

		} catch (Exception e) {
			throw new TransactionFailedException();
		}
		
	}

	private BuyTransaction copy() {
		
		Player oldFrom = this.from.getPlayer();
		Player newFrom = new Player(oldFrom.getUserName(), oldFrom.getId(), oldFrom.getPawn(), oldFrom.getAccount(),
				oldFrom.isReady());

		Account from = new Account(newFrom, this.from.getSaldo(), this.from.getAccUri());

		BuyTransaction copy = new BuyTransaction(from, this.amount);

		return copy;
	}

}
