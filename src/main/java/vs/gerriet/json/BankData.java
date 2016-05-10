package vs.gerriet.json;

import vs.gerriet.model.Bank;

/**
 * Bank data object.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class BankData {

    /**
     * Creates new bank data object for given bank id.
     *
     * @param bank
     *            Bank instance.
     * @return Created bank data object;
     */
    public static BankData createFromBank(final Bank bank) {
        return new BankData(bank.getAccountsUrl(), bank.getTransferUrl());
    }

    /**
     * Url for account access.
     */
    public String accounts;

    /**
     * Url for transfer access.
     */
    public String transfers;

    /**
     * Creates new Bank data object with given information.
     *
     * @param accounts
     *            Url to access the bank's account features.
     * @param transfers
     *            Url to access the bank's transfer features.
     */
    public BankData(final String accounts, final String transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }
}
