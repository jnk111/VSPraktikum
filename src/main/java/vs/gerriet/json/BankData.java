package vs.gerriet.json;

import vs.gerriet.model.bank.Bank;

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
        return new BankData(bank.getAccountsUri(), bank.getTransferUri(), bank.getId().getUri());
    }

    /**
     * Uri for account access.
     */
    public String accounts;

    /**
     * Uri for transfer access.
     */
    public String transfers;

    /**
     * Uri for the bank itself.
     */
    public String bank;

    /**
     * Creates new Bank data object with given information.
     *
     * @param accounts
     *            Uri to access the bank's account features.
     * @param transfers
     *            Uri to access the bank's transfer features.
     * @param bank
     *            Uri to access the bank itself.
     */
    public BankData(final String accounts, final String transfers, final String bank) {
        this.accounts = accounts;
        this.transfers = transfers;
        this.bank = bank;
    }
}
