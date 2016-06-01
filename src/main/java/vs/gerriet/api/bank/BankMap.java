package vs.gerriet.api.bank;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.LazyMap;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.json.BankList;

/**
 * <p>
 * Bank map API class.
 * </p>
 * <p>
 * Changes via this map's map interface method will not change the list within
 * the service itself.
 * </p>
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 *
 */
public class BankMap extends BankBase implements LazyMap<BankId, Bank> {

    /**
     * Contains the bank map singleton instance.
     */
    private static BankMap instance;

    /**
     * Returns the bank map singleton instance.
     *
     * @return Bank map.
     */
    public static synchronized BankMap getInstance() {
        if (BankMap.instance == null) {
            BankMap.instance = new BankMap();
        }
        return BankMap.instance;
    }

    /**
     * Map of transfers, will be loaded within refresh.
     */
    Map<BankId, Bank> map;

    /**
     * Hide default constructor.
     */
    private BankMap() {
        // hide default constructor.
    }

    /**
     * Creates or loads a bank for the given game.
     *
     * @param game
     *            Id of the game.
     * @return Bank API.
     * @throws ApiException
     *             Id bank list loading failed.
     */
    public synchronized Bank createOrLoad(final GameId game) throws ApiException {
        this.load();
        BankId found = null;
        for (final Entry<BankId, Bank> entry : this.map.entrySet()) {
            if (entry.getValue().getGame().equals(game)) {
                found = entry.getKey();
                break;
            }
        }
        if (found != null) {
            return this.get(found);
        }
        final Bank created = new Bank(game);
        this.put(created.getId(), created);
        return created;
    }

    @Override
    public Map<BankId, Bank> getInternalMap() {
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
        final HttpResponse<BankList> result = this.requestGetBankList();
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load bank list from service.");
        }
        // create Bank map from bank uri array
        final Map<BankId, Bank> bankMap = new ConcurrentSkipListMap<>();
        for (final String uri : result.getBody().banks) {
            final BankId id = new BankId(null);
            id.loadUri(uri);
            bankMap.put(id, new Bank(id));
        }
        this.map = bankMap;
    }
}
