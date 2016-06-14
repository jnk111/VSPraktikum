package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.helper.Helper;
import vs.jan.helper.brokerservice.BrokerHelper;
import vs.jan.json.brokerservice.JSONAccount;
import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.model.exception.TransactionRollBackException;
import vs.jan.tools.HttpService;

public class RentTransaction extends Transaction {
	
	public RentTransaction(Player from, Player to, int amount, String bankUri, String gameId, Place place) {
		super();
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.bankUri = bankUri;
		this.gameId = gameId;
		this.place = place;
		this.fromAcc = BrokerHelper.getAccount(this.from.getAccount());
		this.toAcc = BrokerHelper.getAccount(this.to.getAccount());
		this.history = new RentTransaction(this);
	}

	public RentTransaction(RentTransaction trans) {
		super();
		this.from = new Player(trans.getFrom());
		this.to = new Player(trans.getTo());
		this.amount = trans.getAmount();
		this.bankUri = trans.getBankUri();
		this.gameId = trans.getGameId();
		this.place = new Place(trans.getPlace());
		this.fromAcc = new JSONAccount(trans.getFromAcc());
		this.toAcc = new JSONAccount(trans.getToAcc());
	}

	@Override
	public void execute() throws TransactionFailedException {

		try {

			String fromId = BrokerHelper.getID(this.fromAcc.getPlayer());
			String toId = BrokerHelper.getID(this.toAcc.getPlayer());
			
			if(this.fromAcc.getSaldo() >= this.place.getRent().get(this.place.getLevel())) {
				
				String url = this.bankUri + "/" + this.gameId + "/transfer/from/" + fromId + "/to/" + toId + "/" + this.amount;
				HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);
				return;
			} 
		} catch (Exception e) {
			throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
		}
		
		throw new TransactionFailedException(Error.TRANS_FAIL_NEGATIVE_SALDO.getMsg());
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public void rollBack() {
		
		JSONAccount from = Helper.getAccount(this.from.getAccount());
		JSONAccount to = Helper.getAccount(this.to.getAccount());
		RentTransaction history = (RentTransaction) this.history;
		
		if(from.getSaldo() != history.getFromAcc().getSaldo()
				|| to.getSaldo() != history.getToAcc().getSaldo()) {
			
			throw new TransactionRollBackException(Error.TRANS_FAIL.getMsg());
		}		
	}
}
