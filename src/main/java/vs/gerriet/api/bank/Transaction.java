package vs.gerriet.api.bank;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.Lazy;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.json.TransactionInfo;
import vs.gerriet.model.transaction.Transaction.Status;
import vs.gerriet.model.transaction.Transaction.Type;

/**
 * Transaction API class.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Transaction extends BankBase implements Lazy {
    /**
     * Flag used for loading.
     */
    private boolean loaded = false;
    /**
     * Bank this transaction belongs to.
     */
    private final Bank bank;
    /**
     * Transaction map this transaction belongs to.
     */
    private final Transactions transactions;
    /**
     * Id of this transaction.
     */
    private final TransactionId id;
    /**
     * Transaction status.
     */
    private Status status;
    /**
     * Transaction type.
     */
    private Type type;

    /**
     * Loads a new Transaction from the given data.
     *
     * @param bank
     *            Bank this transaction belongs to.
     * @param map
     *            Transaction map this transaction belongs to.
     * @param id
     *            Id of this transaction.
     */
    Transaction(final Bank bank, final Transactions map, final TransactionId id) {
        this.bank = bank;
        this.transactions = map;
        this.id = id;
    }

    /**
     * Creates a new transaction from the given data.
     *
     * @param bank
     *            Bank this transaction belongs to.
     * @param map
     *            Transaction map this transaction belongs to.
     * @param type
     *            Transaction type.
     * @throws ApiException
     *             If creation failed.
     */
    Transaction(final Bank bank, final Transactions map, final Type type) throws ApiException {
        this.bank = bank;
        this.transactions = map;
        HttpResponse<String> result = null;
        if (type != null) {
            result = this.requestCreateTransaction(bank.getId(), type);
        } else {
            result = this.requestCreateTransaction(bank.getId());
        }
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to create transaction");
        }
        this.id = new TransactionId(bank.getId(), null);
        this.id.loadUri(result.getBody());
    }

    /**
     * Commits this transaction.
     * 
     * @throws ApiException
     *             If the request failed.
     */
    public synchronized void commit() throws ApiException {
        final HttpResponse<String> result = this.requestCommitTransaction(this.id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to commit transaction");
        }
    }

    /**
     * Confirms this transaction for the given account.
     * 
     * @param account
     *            Account this transaction should be confirmed for.
     * @throws ApiException
     *             If the request failed.
     */
    public synchronized void confirm(final Account account) throws ApiException {
        final HttpResponse<String> result =
                this.requestConfirmTransaction(this.id, account.getId());
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to confirm transaction");
        }
    }

    /**
     * Returns the bank this transaction belongs to.
     *
     * @return API bank.
     */
    public Bank getBank() {
        return this.bank;
    }

    /**
     * Returns the id of this transaction.
     *
     * @return Transaction id.
     */
    public TransactionId getId() {
        return this.id;
    }

    /**
     * Returns the status of this transaction.
     *
     * @return Transaction status.
     * @throws ApiException
     *             If loading failed.
     */
    public Status getStatus() throws ApiException {
        this.load();
        return this.status;
    }

    /**
     * Returns the transaction map this transaction belongs to.
     *
     * @return Transaction map.
     */
    public Transactions getTransactions() {
        return this.transactions;
    }

    /**
     * Returns the transaction type.
     *
     * @return Transaction type.
     */
    public Type getTransactionType() {
        this.load();
        return this.type;
    }

    @Override
    public synchronized void load() throws ApiException {
        if (!this.loaded) {
            this.refresh();
        }
    }

    @Override
    public synchronized void refresh() throws ApiException {
        final HttpResponse<TransactionInfo> result = this.requestGetTransactionStatus(this.id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load transaction status from bank service.");
        }
        this.updateData(result.getBody());
        this.loaded = true;
    }

    /**
     * Rolls back this transaction.
     * 
     * @throws ApiException
     *             If the request failed.
     */
    public synchronized void rollBack() throws ApiException {
        final HttpResponse<String> result = this.requestRollBackTransaction(this.id);
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to roll back transaction");
        }
    }

    /**
     * Updates internal data from the given transaction info.
     *
     * @param data
     *            Transaction info to update data from.
     */
    private void updateData(final TransactionInfo data) {
        this.status = Status.fromName(data.status);
        switch (data.type) {
            case "2-phase":
                this.type = Type.CHECKED;
                break;
            case "1-phase":
            default:
                this.type = Type.SIMPLE;
                break;
        }
    }
}
