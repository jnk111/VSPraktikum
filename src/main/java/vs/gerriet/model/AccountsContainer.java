package vs.gerriet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.id.UserId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.json.AccountInfo;

/**
 * Container class for accounts.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class AccountsContainer {

    /**
     * Contains the bank this account container belongs to.
     */
    private Bank bank;

    /**
     * We use a sorted map to store accounts to ensure proper ordering. This is
     * required since we can only lock one account at a time and always want to
     * lock / unlock accounts in the same order to prevent deadlocks.
     */
    private final SortedMap<AccountId, Account> accounts = new ConcurrentSkipListMap<>();

    /**
     * Creates a new account within this container. If the account id is already
     * used, <code>false</code> is returned.
     *
     * @param userId
     *            User id.
     * @param account
     *            Account.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     */
    public synchronized boolean createAccount(final UserId userId, final Account account) {
        final AccountId accountId = new AccountId(this.bank.getId(), userId.getBaseData());
        if (this.hasAccount(accountId)) {
            return false;
        }
        this.accounts.put(accountId, account);
        return true;
    }

    /**
     * Removes the account for the given user.
     *
     * @param user
     *            User id.
     */
    public void deleteAccount(final UserId user) {
        this.accounts.remove(user);
    }

    /**
     * Returns the account for the given id.
     *
     * @param id
     *            Account id.
     * @return User account.
     * @throws AccountAccessException
     *             If there is no account for the given user.
     */
    public Account getAccount(final AccountId id) throws AccountAccessException {
        final Account account = this.accounts.get(id);
        // check if the account exists
        if (account == null) {
            throw new AccountAccessException("Account for user '" + id + "' does not exist.");
        }
        return account;
    }

    /**
     * Returns an array containing all account uris within this container.
     *
     * @return Account uris.
     */
    public String[] getAccounts() {
        final Set<?> keys = this.accounts.keySet();
        final AccountId[] accountIds = new AccountId[keys.size()];
        final String[] result = new String[keys.size()];
        for (int i = 0; i < accountIds.length; i++) {
            result[i] = accountIds[i].getUri();
        }
        return result;
    }

    /**
     * Returns information about the given user account.
     *
     * @param accountId
     *            Account id.
     * @return Current balance.
     * @throws AccountAccessException
     *             If the user account could not be locked or if it does not
     *             exist.
     */
    public AccountInfo getInfo(final AccountId accountId) throws AccountAccessException {
        final SortedSet<AccountId> involvedAccounts = new ConcurrentSkipListSet<>();
        involvedAccounts.add(accountId);
        this.lock(involvedAccounts);
        // we know the account exist, otherwise lock() would fail
        final Account acc = this.accounts.get(accountId);
        final AccountInfo res = new AccountInfo(acc.getUser().getUri(), acc.getBalance());
        this.unlock(involvedAccounts);
        return res;
    }

    /**
     * Returns information about the given user account.
     *
     * @param user
     *            User id.
     * @return Current balance.
     * @throws AccountAccessException
     *             If the user account could not be locked or if it does not
     *             exist.
     */
    public AccountInfo getInfo(final UserId user) throws AccountAccessException {
        return this.getInfo(new AccountId(this.bank.getId(), user.getBaseData()));
    }

    /**
     * Checks if the given account id exists.
     *
     * @param id
     *            Account id.
     * @return <code>true</code> if there is an account for the given user,
     *         <code>false</code> otherwise.
     */
    public boolean hasAccount(final AccountId id) {
        return this.accounts.containsKey(id);
    }

    /**
     * <p>
     * Locks the given accounts. If <code>false</code> is returned none of the
     * given accounts where locked. This method uses
     * {@link #lock(SortedSet, long, TimeUnit)} with a timeout of
     * <code>100</code> {@link TimeUnit#MILLISECONDS}.
     * </p>
     * <p>
     * We have the {@link SortedSet} type here to prevent locking the same
     * account twice which would result in a deadlock.
     * </p>
     *
     * @param accountList
     *            Set containing the IDs of all accounts to be locked.
     * @throws AccountAccessException
     *             If a user account could not be locked or if it does not
     *             exist.
     */
    public void lock(final SortedSet<AccountId> accountList) throws AccountAccessException {
        // use default timeout of 100 milliseconds
        this.lock(accountList, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * <p>
     * Locks the given accounts. If <code>false</code> is returned none of the
     * given accounts where locked. Timeout is used for each specified account.
     * The total timeout will be the given timeout multiplied by the number of
     * accounts to be locked.
     * </p>
     * <p>
     * We have the {@link SortedSet} type here to prevent locking the same
     * account twice which would result in a deadlock.
     * </p>
     *
     * @param accountList
     *            Set containing the IDs of all accounts to be locked.
     * @param timeout
     *            Timeout amount.
     * @param unit
     *            Timeout time unit.
     * @throws AccountAccessException
     *             If a user account could not be locked or if it does not
     *             exist.
     */
    public void lock(final SortedSet<AccountId> accountList, final long timeout,
            final TimeUnit unit) throws AccountAccessException {
        // we need this list to keep track of already locked accounts to unlock
        // them when encountering errors
        final List<AccountId> locked = new ArrayList<>();
        // flag to perform cleanup only when required
        boolean cleanup = false;
        try {
            for (final AccountId current : accountList) {
                final Account account = this.getAccount(current);
                // lock existing accounts
                if (account.lock(timeout, unit)) {
                    locked.add(current);
                } else {
                    throw new AccountAccessException(
                            "Failed to lock account for user '" + current + "'");
                }
            }
        } catch (final InterruptedException ex) {
            // set flag for cleanup
            cleanup = true;
            // create account access exception
            throw new AccountAccessException(ex);
        } catch (final AccountAccessException ex) {
            // set flag for cleanup
            cleanup = true;
            // re-throw exception
            throw ex;
        } finally {
            // perform cleanup if the operation was not successful
            if (!cleanup) {
                // unlock all accounts we locked before the error
                for (final AccountId current : locked) {
                    this.accounts.get(current).unlock();
                }
            }
        }
    }

    /**
     * Unlocks all given accounts.
     *
     * @param accountList
     *            Set containing the IDs of all accounts to be locked.
     * @throws AccountAccessException
     *             If a user account does not exist.
     */
    public void unlock(final SortedSet<AccountId> accountList) throws AccountAccessException {
        for (final AccountId current : accountList) {
            final Account account = this.getAccount(current);
            // unlock existing accounts
            account.unlock();
        }
    }
}
