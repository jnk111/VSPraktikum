package vs.gerriet.api.bank;

import vs.gerriet.api.Lazy;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.TransferId;

public class Transaction extends BankBase implements Lazy {

    Transaction(final Bank bank, final TransactionMap list, final TransferId id) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void load() throws ApiException {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh() throws ApiException {
        // TODO Auto-generated method stub

    }
    // TODO @gerriet-hinrichs: create transaction
    // TODO @gerriet-hinrichs: commit transaction
    // TODO @gerriet-hinrichs: roll-back transaction

}
