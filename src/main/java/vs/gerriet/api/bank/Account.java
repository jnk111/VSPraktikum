package vs.gerriet.api.bank;

import vs.gerriet.api.Lazy;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.AccountId;

public class Account extends BankBase implements Lazy {
    /**
     * Contains the bank that belongs to this list.
     */
    private final Bank bank;
    /**
     * Contains the account list this account is part of.
     */
    private final AccountList list;
    /**
     * Contains.
     */
    private final AccountId id;

    Account(final Bank bank, final AccountList list, final AccountId id) {
        this.bank = bank;
        this.list = list;
        this.id = id;
    }

    @Override
    public void load() throws ApiException {
        // TODO Auto-generated method stub
    }

    @Override
    public void refresh() throws ApiException {
        // TODO Auto-generated method stub
    }

    // TODO @gerriet-hinrichs: add account
}
