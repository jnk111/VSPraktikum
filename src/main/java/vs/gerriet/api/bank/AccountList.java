package vs.gerriet.api.bank;

import java.util.Map;

import vs.gerriet.api.LazyMap;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.bank.AccountId;

/**
 * <p>
 * Account map API class.
 * </p>
 * <p>
 * This map is not modifiable. Except when calling {@link #refresh()}.
 * </p>
 */
public class AccountList extends BankBase implements LazyMap<AccountId, Account> {
    /**
     * Map of accounts, will be loaded within refresh. Cannot be modified.
     */
    private Map<AccountId, Account> map;

    /**
     * Bank this transfer list belongs to.
     */
    private final Bank bank;

    /**
     * Creates a new lazy account list for the given bank.
     *
     * @param bank
     *            Bank this list belongs to.
     */
    AccountList(final Bank bank) {
        this.bank = bank;
    }

    @Override
    public Map<AccountId, Account> getInternalMap() {
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
        // final HttpResponse<vs.gerriet.json.AccountList> result =
        // this.requestGetAccountList(this.bank.getId());
        // if (result == null || result.getStatus() != 200) {
        // throw new ApiException("Failed to load bank list from service.");
        // }
        // // create Bank map from bank uri array
        // final Map<AccountId, Account> accountMap = new
        // ConcurrentSkipListMap<>();
        // for (final String uri : result.getBody().accounts) {
        // final AccountId id = new AccountId(this.bank.getId(), null);
        // id.loadUri(uri);
        // accountMap.put(id, new Account(this.bank, this, id));
        // }
        // // make list read only
        // this.map = Collections.unmodifiableMap(accountMap);
    }
}
