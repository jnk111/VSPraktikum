package vs.gerriet.api.bank;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.Lazy;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.json.BankData;

/**
 * Bank API class.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Bank extends BankBase implements Lazy {

    /**
     * Flag if {@link #load()} has been called yet.
     */
    private boolean loaded = false;
    /**
     * <p>
     * Contains the bank's id.
     * </p>
     * <p>
     * This member field is not part of lazy loading.
     * </p>
     */
    private BankId id;

    /**
     * Contains accounts from this bank.
     */
    private AccountList accounts;
    /**
     * Contains transactions from this bank.
     */
    private TransactionMap transactions;

    /**
     * Contains transfers for this bank.
     */
    private TransferMap transfers;

    /**
     * Game this bank belongs to.
     */
    private GameId game;

    /**
     * Loads the bank with the given id from the bank service.
     *
     * @param id
     *            Bank id.
     * @throws ApiException
     *             If the bank data could not be loaded from the bank service.
     */
    Bank(final BankId id) throws ApiException {
        this.id = id;
    }

    /**
     * <p>
     * Creates (if it does not exist) or loads the bank for the given game.
     * </p>
     * <p>
     * This constructor is not lazy.
     * </p>
     *
     * @param id
     *            Game id.
     * @throws ApiException
     *             If the bank could not be created on the bank service.
     */
    Bank(final GameId id) throws ApiException {
        final HttpResponse<BankData> result = this.requestCreateBank(id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to create bank for game '" + id.getUri() + "'.");
        }
        this.refreshData(result.getBody());
        this.loaded = true;
    }

    /**
     * Returns the accounts that belong to this bank.
     *
     * @return Account list.
     */
    public AccountList getAccounts() {
        this.load();
        return this.accounts;
    }

    /**
     * Returns the game this bank belongs to.
     *
     * @return Game id.
     */
    public GameId getGame() {
        return this.game;
    }

    /**
     * Returns the id of this bank.
     *
     * @return Bank id.
     */
    public BankId getId() {
        return this.id;
    }

    /**
     * Returns the transactions that belong to this bank.
     *
     * @return Transaction list.
     */
    public TransactionMap getTransactions() {
        this.load();
        return this.transactions;
    }

    /**
     * Returns the transfers that belong to this bank.
     *
     * @return Transfer list.
     */
    public TransferMap getTransfers() {
        this.load();
        return this.transfers;
    }

    @Override
    public synchronized void load() throws ApiException {
        if (this.loaded) {
            return;
        }
        this.refresh();
        this.loaded = true;
    }

    @Override
    public synchronized void refresh() throws ApiException {
        final HttpResponse<BankData> result = this.requestGetBankData(this.id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Bank data for bank '" + this.id.getUri()
                    + "' could not be loaded from bank service.");
        }
        this.refreshData(result.getBody());
    }

    /**
     * Refreshes the internal data of this bank API instance.
     *
     * @param data
     *            Data to update this instance from.
     */
    private void refreshData(final BankData data) {
        this.id = new BankId(null);
        this.id.loadUri(data.bank);
        this.game = new GameId(null);
        this.game.loadUri(data.game);
        this.transfers = new TransferMap(this);
        this.accounts = new AccountList(this);
        this.transactions = new TransactionMap(this);
    }

}
