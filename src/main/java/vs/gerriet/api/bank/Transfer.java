package vs.gerriet.api.bank;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.Lazy;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.TransferInfo;
import vs.gerriet.model.bank.transaction.AtomicOperation.Type;

/**
 * Transfer API class.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Transfer extends BankBase implements Lazy {
    /**
     * Flag used for loading.
     */
    private boolean loaded = false;
    /**
     * Bank this transfer belongs to.
     */
    private final Bank bank;
    /**
     * Transfer map this transfer belongs to.
     */
    private final Transfers map;
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
     * Creates a new transfer on the server with the given data.
     *
     * @param bank
     *            Bank this transfer will be performed on.
     * @param map
     *            Map that will belong to this transfer.
     * @param from
     *            Account money is withdrawn from (may be <code>null</code> for
     *            the bank itself).
     * @param to
     *            Account money is deposited on (may be <code>null</code> for
     *            the bank itself).
     * @param amount
     *            Amount to transfer.
     * @param reason
     *            Transfer reason.
     * @param transaction
     *            Transaction to add this transfer to (may be <code>null</code>
     *            for no transaction).
     * @throws ApiException
     *             If transfer creation failed.
     */
    Transfer(final Bank bank, final Transfers map, final Account from, final Account to,
            final int amount, final String reason, final Transaction transaction)
            throws ApiException {
        this.bank = bank;
        this.map = map;
        HttpResponse<TransferInfo> result = null;
        if (transaction != null) {
            // transfer with transaction
            if (from == null && to != null) {
                result = this.requestPerformTransfer(to.getId(), Type.DEPOSIT, amount, reason,
                        transaction.getId());
            } else if (from != null && to != null) {
                result = this.requestPerformTransfer(from.getId(), Type.WITHDRAW, amount, reason,
                        transaction.getId());
            } else if (from != null && to != null) {
                result = this.requestPerformTransfer(from.getId(), to.getId(), amount, reason,
                        transaction.getId());
            }
        } else {
            // transfer without transaction
            if (from == null && to != null) {
                result = this.requestPerformTransfer(to.getId(), Type.DEPOSIT, amount, reason);
            } else if (from != null && to != null) {
                result = this.requestPerformTransfer(from.getId(), Type.WITHDRAW, amount, reason);
            } else if (from != null && to != null) {
                result = this.requestPerformTransfer(from.getId(), to.getId(), amount, reason);
            }
        }
        if (result == null || (result.getStatus() != 201 && result.getStatus() != 403)) {
            throw new ApiException("Failed to create transfer.");
        }
        // load id of the new created transfer
        this.id = new TransferId(bank.getId(), null);
        this.id.loadUri(result.getBody().uri);
        // load data from created instance
        this.loadData(result.getBody());
        this.loaded = true;
    }

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
    Transfer(final Bank bank, final Transfers map, final TransferId id) {
        this.bank = bank;
        this.map = map;
        this.id = id;
    }

    /**
     * Returns the transfer amount.
     *
     * @return Transfer amount.
     * @throws ApiException
     *             If loading failed.
     */
    public int getAmount() throws ApiException {
        this.load();
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
     * @throws ApiException
     *             If loading failed.
     */
    public Account getFrom() throws ApiException {
        this.load();
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
     * @throws ApiException
     *             If loading failed.
     */
    public String getReason() throws ApiException {
        this.load();
        return this.reason;
    }

    /**
     * Returns the account this transfer deposits money to.
     *
     * @return Account this transfer deposits money to. Will be
     *         <code>null</code> for the bank itself.
     * @throws ApiException
     *             If loading failed.
     * 
     */
    public Account getTo() throws ApiException {
        this.load();
        return this.to;
    }

    /**
     * Returns the map for this transfer.
     *
     * @return Transfer map. May be <code>null</code>.
     */
    public Transfers getTransfers() {
        return this.map;
    }

    /**
     * Returns if this transfer failed. A transfer will always fail if the
     * associated transaction fails, even if the transfer itself is not the
     * actual cause.
     *
     * @return Failed flag.
     * @throws ApiException
     *             If loading failed.
     */
    public boolean isFailed() throws ApiException {
        this.load();
        return this.failed;
    }

    /**
     * Returns if this transfer is already performed or pending (transaction
     * still running).
     *
     * @return Pending flag.
     * @throws ApiException
     *             If loading failed.
     */
    public boolean isPending() throws ApiException {
        this.load();
        return this.pending;
    }

    @Override
    public synchronized void load() throws ApiException {
        if (!this.loaded) {
            this.refresh();
        }
    }

    @Override
    public synchronized void refresh() throws ApiException {
        final HttpResponse<TransferInfo> result = this.requestGetTransferInfo(this.id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load Transfer details from bank service.");
        }
        this.loadData(result.getBody());
        this.loaded = true;
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
}
