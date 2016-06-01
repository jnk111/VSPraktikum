package vs.gerriet.api.bank;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.LazyMap;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.json.TransactionList;
import vs.gerriet.model.transaction.Transaction.Type;

/**
 * <p>
 * Transaction map API class.
 * </p>
 * <p>
 * Modification of this map does not affect the remove list.
 * </p>
 */
public class Transactions extends BankBase implements LazyMap<TransactionId, Transaction> {
    /**
     * Map of transfers, will be loaded within refresh.
     */
    private Map<TransactionId, Transaction> map;

    /**
     * Bank this transaction map belongs to.
     */
    private final Bank bank;

    /**
     * Creates a new lazy transaction map for the given bank.
     *
     * @param bank
     *            Bank this map belongs to.
     */
    Transactions(final Bank bank) {
        this.bank = bank;
    }

    /**
     * Creates a new simple (1-phase) transaction.
     * 
     * @return Created transaction.
     * @throws ApiException
     *             If creation failed.
     */
    public Transaction createTransaction() throws ApiException {
        return this.createTransaction(Type.SIMPLE);
    }

    /**
     * Creates a new transaction of the given type.
     * 
     * @param type
     *            Transaction type.
     * @return Created transaction.
     * @throws ApiException
     *             If creation failed.
     */
    public synchronized Transaction createTransaction(final Type type) throws ApiException {
        this.load();
        final Transaction transaction = new Transaction(this.bank, this, type);
        this.map.put(transaction.getId(), transaction);
        return transaction;
    }

    /**
     * Returns the bank that belongs to this transfer map.
     *
     * @return Bank for this transfer map.
     */
    public Bank getBank() {
        return this.bank;
    }

    @Override
    public Map<TransactionId, Transaction> getInternalMap() {
        return this.map;
    }

    @Override
    public synchronized void load() throws ApiException {
        if (this.map == null) {
            this.refresh();
        }
    }

    @Override
    public synchronized void refresh() throws ApiException {
        final HttpResponse<TransactionList> result =
                this.requestGetTransactionList(this.bank.getId());
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load transaction list from bank service.");
        }
        // create transaction map from transaction uri array
        final Map<TransactionId, Transaction> transactionMap = new ConcurrentSkipListMap<>();
        for (final String uri : result.getBody().transactions) {
            final TransactionId id = new TransactionId(this.bank.getId(), null);
            id.loadUri(uri);
            transactionMap.put(id, new Transaction(this.bank, this, id));
        }
        this.map = transactionMap;
    }
}
