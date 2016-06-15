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

public class BankSellTransaction extends Transaction {


	public BankSellTransaction(Player to, int amount, String bankUri, String gameId, Place place) {

		this.to = to;
		this.amount = amount;
		this.place = place;
		this.bankUri = bankUri;
		this.gameId = gameId;
		this.place = place;
		this.toAcc = BrokerHelper.getAccount(to.getAccount());
		this.history = new BankSellTransaction(this);
	}

	public BankSellTransaction(BankSellTransaction trans) {

		this.to = trans.getTo();
		this.amount = trans.getAmount();
		this.place = new Place(trans.getPlace());
		this.bankUri = trans.getBankUri();
		this.gameId = trans.getGameId();
		this.place = new Place(trans.getPlace());
		this.toAcc = new JSONAccount(trans.getToAcc());
	}

	@Override
	public void execute() throws TransactionFailedException {

		try {

			String toId = BrokerHelper.getID(this.toAcc.getPlayer());
			String url = this.bankUri + "/" + this.gameId + "/transfer/to/" + toId + "/" + this.amount;
			HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);
			this.place.setHypo(true);

		} catch (Exception e) {
			throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
		}
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	@Override
	public void rollBack() {

		JSONAccount to = Helper.getAccount(this.to.getAccount());
		BankSellTransaction history = (BankSellTransaction) this.history;

		if (to.getSaldo() != history.getToAcc().getSaldo()) {

			this.place.setHypo(false);
			throw new TransactionRollBackException(Error.TRANS_FAIL.getMsg());
		}

	}
}
