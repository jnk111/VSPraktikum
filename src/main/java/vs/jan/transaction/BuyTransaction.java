package vs.jan.transaction;

import java.net.HttpURLConnection;

import vs.jan.helper.Helper;
import vs.jan.helper.brokerservice.BrokerHelper;
import vs.jan.json.brokerservice.JSONAccount;
import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Estate;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.model.exception.TransactionRollBackException;
import vs.jan.tools.HttpService;
import vs.jan.model.exception.Error;

public class BuyTransaction extends Transaction {

	public BuyTransaction(Player player, int amount, String bankUri, String gameId, Estate place) {
		this.from = player;
		this.amount = amount;
		this.history = null;
		this.bankUri = bankUri;
		this.gameId = gameId;
		this.place = place;
		this.fromAcc = BrokerHelper.getAccount(player.getAccount());
		this.history = new BuyTransaction(this);
	}

	public BuyTransaction(BuyTransaction trans) {
		this.from = new Player(trans.getFrom());
		this.amount = trans.getAmount();
		this.bankUri = trans.getBankUri();
		this.gameId = trans.getGameId();
		this.place = new Estate(trans.getPlace());
		this.fromAcc = new JSONAccount(trans.getFromAcc());

	}

	@Override
	public void execute() throws TransactionFailedException {

		try {

			JSONAccount accFrom = BrokerHelper.getAccount(this.from.getAccount());
			String fromId = BrokerHelper.getID(accFrom.getPlayer());

			if (accFrom.getSaldo() >= this.amount) {
				String url = this.bankUri + "/" + this.gameId + "/transfer/from/" + fromId + "/" + this.amount;
				HttpService.post(url, null, HttpURLConnection.HTTP_CREATED);
				this.place.setOwner(this.from);
				return;

			}
		} catch (Exception e) {
			throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
		}
		
		throw new TransactionFailedException(Error.TRANS_FAIL_NEGATIVE_SALDO.getMsg());
	}

	@Override
	public void rollBack() throws TransactionRollBackException {

		JSONAccount from = Helper.getAccount(this.from.getAccount());
		BuyTransaction history = (BuyTransaction) this.history;

		if (from.getSaldo() != history.getFromAcc().getSaldo()) {
			this.place.setOwner(null);
			throw new TransactionRollBackException(Error.ROLL_BACK_FAILED.getMsg());
		}
	}

	public Estate getPlace() {
		return place;
	}

	public void setPlace(Estate place) {
		this.place = place;
	}
}
