package vs.gerriet.api.bank;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.Lazy;
import vs.gerriet.exception.ApiException;
import vs.gerriet.id.UserId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.json.AccountInfo;

/**
 * Account API class.
 * 
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Account extends BankBase implements Lazy {
    /**
     * Flag for loading.
     */
    private boolean loaded = false;
    /**
     * Contains the bank that belongs to this list.
     */
    private final Bank bank;
    /**
     * Contains the account map this account is part of.
     */
    private final Accounts map;
    /**
     * Contains.
     */
    private final AccountId id;
    /**
     * Account user id.
     */
    private UserId player;
    /**
     * Account balance.
     */
    private int balance;

    /**
     * Loads an account from the given data.
     *
     * @param bank
     *            Bank the account belongs to.
     * @param map
     *            Account map this account belongs to.
     * @param id
     *            Account id.
     */
    Account(final Bank bank, final Accounts map, final AccountId id) {
        this.bank = bank;
        this.map = map;
        this.id = id;
    }

    /**
     * Creates an account from the given data.
     *
     * @param bank
     *            Bank the account belongs to.
     * @param map
     *            Account map this account belongs to.
     * @param user
     *            User for the account.
     * @param balance
     *            Starting balance.
     * @throws ApiException
     *             If creation failed.
     */
    Account(final Bank bank, final Accounts map, final UserId user, final int balance)
            throws ApiException {
        this.bank = bank;
        this.map = map;
        final AccountInfo data = new AccountInfo(user.getUri(), balance, null);
        final HttpResponse<AccountInfo> result = this.requestCreateAccount(bank.getId(), data);
        if (result == null || (result.getStatus() != 201 && result.getStatus() != 409)) {
            throw new ApiException("Failed to create account.");
        }
        this.id = new AccountId(bank.getId(), null);
        this.id.loadUri(result.getBody().uri);
        this.loadData(result.getBody());
        this.loaded = true;
    }

    /**
     * Returns the account map this account is part of.
     *
     * @return Account map.
     */
    public Accounts getAccounts() {
        return this.map;
    }

    /**
     * Returns this account's balance.
     *
     * @return Account balance.
     */
    public int getBalance() {
        this.load();
        return this.balance;
    }

    /**
     * Returns the bank that belongs to this account.
     *
     * @return Bank for this account.
     */
    public Bank getBank() {
        return this.bank;
    }

    /**
     * Returns this account's id.
     *
     * @return Account id.
     */
    public AccountId getId() {
        return this.id;
    }

    /**
     * Returns the id of the player that belongs to this account.
     *
     * @return Account player id.
     */
    public UserId getPlayer() {
        this.load();
        return this.player;
    }

    @Override
    public synchronized void load() throws ApiException {
        if (!this.loaded) {
            this.refresh();
        }
    }

    @Override
    public synchronized void refresh() throws ApiException {
        final HttpResponse<AccountInfo> result = this.requestGetAccount(this.id);
        this.loadData(result.getBody());
        this.loaded = true;
    }

    /**
     * Loads account data into this instance.
     *
     * @param data
     *            Data to be loaded.
     */
    private void loadData(final AccountInfo data) {
        this.balance = data.balance;
        this.player = new UserId(null);
        this.player.loadUri(data.player);
    }
}
