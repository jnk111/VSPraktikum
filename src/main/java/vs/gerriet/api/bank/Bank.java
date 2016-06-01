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
    private boolean initialized = false;
    /**
     * <p>
     * Contains the bank's id.
     * </p>
     * <p>
     * This member field is not part of lazy loading.
     * </p>
     */
    private BankId id;

    // TODO @gerriet-hinrichs: Add
    // private AccountList accounts;
    // TODO @gerriet-hinrichs: Add
    // private TransactionList transactions;
    // TODO @gerriet-hinrichs: Add
    // private TransferList transfers;

    /**
     * Loads the bank with the given id from the bank service.
     *
     * @param id
     *            Bank id.
     * @throws ApiException
     *             If the bank data could not be loaded from the bank service.
     */
    public Bank(final BankId id) throws ApiException {
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
    public Bank(final GameId id) throws ApiException {
        final HttpResponse<BankData> result = this.requestCreateBank(id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to create bank for game '" + id.getUri() + "'.");
        }
        this.refreshData(result.getBody());
        this.initialized = true;
    }

    /**
     * Returns the id of this bank.
     * 
     * @return Bank id.
     */
    public BankId getId() {
        return this.id;
    }

    @Override
    public void load() throws ApiException {
        if (this.initialized) {
            return;
        }
        this.refresh();
        this.initialized = true;
    }

    @Override
    public void refresh() throws ApiException {
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
        // TODO @gerriet-hinrichs: load missing fields
    }
}
