package vs.gerriet.api.bank;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.Lazy;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.TransferInfo;

/**
 * Transfer API class.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Transfer extends BankBase implements Lazy {
    /**
     * Flag used for loading.
     */
    private final boolean loaded = false;
    /**
     * Bank this transfer belongs to.
     */
    private final Bank bank;
    /**
     * Transfer map this transfer belongs to.
     */
    private final TransferMap map;
    /**
     * Transfer id.
     */
    private final TransferId id;
    /**
     * Contains transfer amount.
     */
    private int amount;
    /**
     * Account this transfer withdraws money from. Will be <code>null</code> for
     * the bank itself.
     */
    private Account from;
    /**
     * Account this transfer deposits money on. Will be <code>null</code> for
     * the bank itself.
     */
    private Account to;
    /**
     * Reason for this transfer.
     */
    private String reason;
    /**
     * Pending flag of this transfer.
     */
    private boolean pending;
    /**
     * Failed flag for this transfer.
     */
    private boolean failed;

    /**
     * Creates a new Transfer lazy from the given data.
     *
     * @param bank
     *            Bank API.
     * @param map
     *            Transfer map.
     * @param id
     *            Transfer id.
     */
    Transfer(final Bank bank, final TransferMap map, final TransferId id) {
        this.bank = bank;
        this.map = map;
        this.id = id;
    }

    /**
     * Returns the transfer amount.
     *
     * @return Transfer amount.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Returns bank API for this transfer.
     *
     * @return Bank API.
     */
    public Bank getBank() {
        return this.bank;
    }

    /**
     * Returns the account this transfer withdraws money from.
     *
     * @return Account this transfer withdraws money from. Will be
     *         <code>null</code> for the bank itself.
     */
    public Account getFrom() {
        return this.from;
    }

    /**
     * Returns the id of this transfer.
     *
     * @return Transfer id. May be <code>null</code>.
     */
    public TransferId getId() {
        return this.id;
    }

    /**
     * Returns the reason for this transfer.
     *
     * @return Transfer reason.
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Returns the account this transfer deposits money to.
     *
     * @return Account this transfer deposits money to. Will be
     *         <code>null</code> for the bank itself.
     */
    public Account getTo() {
        return this.to;
    }

    /**
     * Returns the map for this transfer.
     *
     * @return Transfer map. May be <code>null</code>.
     */
    public TransferMap getTransferMap() {
        return this.map;
    }

    /**
     * Returns if this transfer failed. A transfer will always fail if the
     * associated transaction fails, even if the transfer itself is not the
     * actual cause.
     *
     * @return Failed flag.
     */
    public boolean isFailed() {
        return this.failed;
    }

    /**
     * Returns if this transfer is already performed or pending (transaction
     * still running).
     *
     * @return Pending flag.
     */
    public boolean isPending() {
        return this.pending;
    }

    @Override
    public void load() throws ApiException {
        if (!this.loaded) {
            this.refresh();
        }
    }

    @Override
    public void refresh() throws ApiException {
        final HttpResponse<TransferInfo> result = this.requestGetTransferInfo(this.id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load Transfer details from bank service.");
        }
        this.loadData(result.getBody());
    }

    /**
     * Loads this instance from the given JSON data.
     *
     * @param data
     *            JSON data to be loaded.
     */
    private void loadData(final TransferInfo data) {
        this.amount = data.amount;
        this.reason = data.reason;
        this.pending = data.pending;
        this.failed = data.failed;
        this.from = null;
        if (data.from != null) {
            final AccountId accountId = new AccountId(this.bank.getId(), null);
            accountId.loadUri(data.from);
            this.from = this.bank.getAccounts().get(accountId);
        }
        this.to = null;
        if (data.to != null) {
            final AccountId accountId = new AccountId(this.bank.getId(), null);
            accountId.loadUri(data.to);
            this.to = this.bank.getAccounts().get(accountId);
        }
    }

    // TODO @gerriet-hinrichs: create transfer.
}
