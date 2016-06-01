package vs.gerriet.api.bank;

import java.util.Map;

import vs.gerriet.api.LazyMap;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.TransactionId;

/**
 * <p>
 * Transaction map API class.
 * </p>
 * <p>
 * Modification of this map does not affect the remove list.
 * </p>
 */
public class TransactionMap extends BankBase implements LazyMap<TransactionId, Transaction> {
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
    TransactionMap(final Bank bank) {
        this.bank = bank;
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
    public void load() throws ApiException {
        if (this.map == null) {
            this.refresh();
        }
    }

    @Override
    public void refresh() throws ApiException {
        // TODO @gerriet-hinrichs: loading
        // final HttpResponse<vs.gerriet.json.TransferList> result =
        // this.requestGetTransferList(this.bank.getId());
        // if (result == null || result.getStatus() != 200) {
        // throw new ApiException("Failed to load bank list from service.");
        // }
        // // create Bank map from bank uri array
        // final Map<TransferId, Transfer> transferMap = new
        // ConcurrentSkipListMap<>();
        // for (final String uri : result.getBody().transfers) {
        // final TransferId id = new TransferId(this.bank.getId(), null);
        // id.loadUri(uri);
        // transferMap.put(id, new Transfer(this.bank, this, id));
        // }
        // // make list read only
        // this.map = transferMap;
    }
}
