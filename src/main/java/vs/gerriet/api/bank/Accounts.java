package vs.gerriet.api.bank;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.LazyMap;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.UserId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.json.AccountList;

/**
 * <p>
 * Account map API class.
 * </p>
 * <p>
 * Modification of this map does not affect the remove list.
 * </p>
 */
public class Accounts extends BankBase implements LazyMap<AccountId, Account> {
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
    Accounts(final Bank bank) {
        this.bank = bank;
    }

    /**
     * Creates a new user account.
     * 
     * @param user
     *            User to create the account for.
     * @param balance
     *            Starting balance.
     * @return Created account.
     * @throws ApiException
     *             If creation failed.
     */
    public synchronized Account createAccount(final UserId user, final int balance)
            throws ApiException {
        final Account account = new Account(this.bank, this, user, balance);
        this.put(account.getId(), account);
        return account;
    }

    @Override
    public Map<AccountId, Account> getInternalMap() {
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
        final HttpResponse<AccountList> result = this.requestGetAccountList(this.bank.getId());
        if (result == null || result.getStatus() != 200) {
            throw new ApiException("Failed to load account list from service.");
        }
        // create account map from account uri array
        final Map<AccountId, Account> accountMap = new ConcurrentSkipListMap<>();
        for (final String uri : result.getBody().accounts) {
            final AccountId id = new AccountId(this.bank.getId(), null);
            id.loadUri(uri);
            accountMap.put(id, new Account(this.bank, this, id));
        }
        this.map = accountMap;
    }
}
