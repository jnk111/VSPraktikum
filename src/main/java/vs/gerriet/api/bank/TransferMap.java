package vs.gerriet.api.bank;

import java.util.Collections;
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
 * This map is not modifiable. Except when calling {@link #refresh()}.
 * </p>
 */
public class TransferMap extends BankBase implements LazyMap<TransferId, Transfer> {
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
    TransferMap(final Bank bank) {
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
    public Map<TransferId, Transfer> getInternalMap() {
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
        final HttpResponse<TransferList> result = this.requestGetTransferList(this.bank.getId());
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load transfer list from bank service.");
        }
        // create Bank map from bank uri array
        final Map<TransferId, Transfer> transferMap = new ConcurrentSkipListMap<>();
        for (final String uri : result.getBody().transfers) {
            final TransferId id = new TransferId(this.bank.getId(), null);
            id.loadUri(uri);
            transferMap.put(id, new Transfer(this.bank, this, id));
        }
        // make list read only
        this.map = Collections.unmodifiableMap(transferMap);
    }

    // TODO @gerriet-hinrichs: create transfer
}
