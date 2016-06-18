package vs.jan.transaction;

import vs.jan.helper.Helper;
import vs.jan.json.brokerservice.JSONAccount;
import vs.jan.model.boardservice.Player;
import vs.jan.model.brokerservice.Estate;
import vs.jan.model.exception.Error;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.model.exception.TransactionRollBackException;

public class HouseTransaction extends BuyTransaction {

	public HouseTransaction(Player player, Integer amount, String bankUri, String gameId, Estate place) {
		super(player, amount, bankUri, gameId, place);
	}
	
	
	@Override
	public void execute() throws TransactionFailedException {
		super.execute();
		this.place.setHouses(this.place.getHouses() + 1);
	}


	@Override
	public void rollBack() throws TransactionRollBackException{
		JSONAccount from = Helper.getAccount(this.from.getAccount());
		BuyTransaction history = (BuyTransaction) this.history;

		if (from.getSaldo() != history.getFromAcc().getSaldo()) {
			this.place.setHouses(history.getPlace().getHouses());
			throw new TransactionRollBackException(Error.ROLL_BACK_FAILED.getMsg());
		}
	}
}
