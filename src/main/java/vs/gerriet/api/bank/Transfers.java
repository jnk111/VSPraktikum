package vs.gerriet.api.bank;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.LazyMap;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.TransferList;

/**
 * <p>
 * Transfer map API class.
 * </p>
 * <p>
 * Changes via this map's map interface method will not change the list within
 * the service itself.
 * </p>
 */
public class Transfers extends BankBase implements LazyMap<TransferId, Transfer> {
    /**
     * Map of transfers, will be loaded within refresh. Cannot be modified.
     */
    private Map<TransferId, Transfer> map;

    /**
     * Bank this transfer map belongs to.
     */
    private final Bank bank;

    /**
     * Creates a new lazy transfer map for the given bank.
     *
     * @param bank
     *            Bank this map belongs to.
     */
    Transfers(final Bank bank) {
        this.bank = bank;
    }

    /**
     * Creates a new transfer.
     *
     * @param from
     *            Account to withdraw money from.
     * @param to
     *            Account to deposit money to.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @return Created transfer.
     * @throws ApiException
     *             If creation failed.
     */
    public Transfer createTransfer(final Account from, final Account to, final int amount,
            final String reason) throws ApiException {
        return this.createTransfer(from, to, amount, reason, null);
    }

    /**
     * Creates a new transfer.
     *
     * @param from
     *            Account to withdraw money from.
     * @param to
     *            Account to deposit money to.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @param transaction
     *            Transaction this transfer will be added to.
     * @return Created transfer.
     * @throws ApiException
     *             If creation failed.
     */
    public synchronized Transfer createTransfer(final Account from, final Account to,
            final int amount, final String reason, final Transaction transaction)
            throws ApiException {
        final Transfer transfer =
                new Transfer(this.bank, this, from, to, amount, reason, transaction);
        this.put(transfer.getId(), transfer);
        return transfer;
    }

    /**
     * Creates a new transfer that will remove money from the given account.
     *
     * @param account
     *            Account to withdraw money from.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @return Created transfer.
     * @throws ApiException
     *             If creation failed.
     */
    public Transfer createTransferFrom(final Account account, final int amount, final String reason)
            throws ApiException {
        return this.createTransfer(account, null, amount, reason, null);
    }

    /**
     * Creates a new transfer that will remove money from the given account.
     *
     * @param account
     *            Account to withdraw money from.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @param transaction
     *            Transaction this transfer will be added to.
     * @return Created transfer.
     * @throws ApiException
     *             If creation failed.
     */
    public Transfer createTransferFrom(final Account account, final int amount, final String reason,
            final Transaction transaction) throws ApiException {
        return this.createTransfer(account, null, amount, reason, transaction);
    }

    /**
     * Creates a new transfer that will add money to the given account.
     *
     * @param account
     *            Account to deposit money to.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @return Created transfer.
     * @throws ApiException
     *             If creation failed.
     */
    public Transfer createTransferTo(final Account account, final int amount, final String reason)
            throws ApiException {
        return this.createTransfer(null, account, amount, reason, null);
    }

    /**
     * Creates a new transfer that will add money to the given account.
     *
     * @param account
     *            Account to deposit money to.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @param transaction
     *            Transaction this transfer will be added to.
     * @return Created transfer.
     * @throws ApiException
     *             If creation failed.
     */
    public Transfer createTransferTo(final Account account, final int amount, final String reason,
            final Transaction transaction) throws ApiException {
        return this.createTransfer(null, account, amount, reason, transaction);
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
    public Map<TransferId, Transfer> getInternalMap() {
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
        final HttpResponse<TransferList> result = this.requestGetTransferList(this.bank.getId());
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load transfer list from bank service.");
        }
        // create transfer map from transfer uri array
        final Map<TransferId, Transfer> transferMap = new ConcurrentSkipListMap<>();
        for (final String uri : result.getBody().transfers) {
            final TransferId id = new TransferId(this.bank.getId(), null);
            id.loadUri(uri);
            transferMap.put(id, new Transfer(this.bank, this, id));
        }
        this.map = transferMap;
    }
}
