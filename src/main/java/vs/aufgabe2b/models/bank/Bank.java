package vs.aufgabe2b.models.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import vs.aufgabe2b.exceptions.bank.AccountAccessException;
import vs.aufgabe2b.transaction.AtomicOperation;

/**
 * <p>
 * Thread save bank with transaction support.
 * </p>
 * <p>
 * Account interaction is done by using locks on per account basis to allow
 * multiple interactions that don't influence each other at the same time.
 * </p>
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class Bank {

    /**
     * Contains the bank's id.
     */
    private final String id;

    /**
     * Contains the account url for this bank.
     */
    private String accountsUrl;

    /**
     * Contains the transfer url for this bank.
     */
    private String transferUrl;

    /**
     * We use a sorted map to store accounts to ensure proper ordering. This is
     * required since we can only lock one account at a time and always want to
     * lock / unlock accounts in the same order to prevent deadlocks.
     */
    private final SortedMap<String, Account> accounts;

    /**
     * Creates a new bank without any accounts.
     */
    public Bank(final String id) {
        // create an account map and add synchronization
        this.accounts = Collections.synchronizedSortedMap(new TreeMap<String, Account>());
        this.id = id;
    }

    /**
     * Creates a new account for the given user with balance <code>0</code>.
     *
     * @param user
     *            User url.
     * @return <code>true</code> on success, <code>false</code> if an account
     *         for the given user already exists.
     * @see #createAccount(String, int)
     */
    public boolean createAccount(final String user) {
        return this.createAccount(user, 0);
    }

    /**
     * Creates a new account for the given user with the given balance.
     *
     * @param user
     *            User url.
     * @param balance
     *            Account balance.
     * @return <code>true</code> on success, <code>false</code> if an account
     *         for the given user already exists.
     */
    public synchronized boolean createAccount(final String user, final int balance) {
        if (this.hasAccount(user)) {
            return false;
        }
        this.accounts.put(user, new Account(user, balance));
        return true;
    }

    /**
     * Deletes the account for the given user.
     *
     * @param user
     *            User url.
     */
    public void deleteAccount(final String user) {
        this.accounts.remove(user);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Bank other = (Bank) obj;
        if (this.accounts == null) {
            if (other.accounts != null) {
                return false;
            }
        } else if (!this.accounts.equals(other.accounts)) {
            return false;
        }
        if (this.accountsUrl == null) {
            if (other.accountsUrl != null) {
                return false;
            }
        } else if (!this.accountsUrl.equals(other.accountsUrl)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.transferUrl == null) {
            if (other.transferUrl != null) {
                return false;
            }
        } else if (!this.transferUrl.equals(other.transferUrl)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the accounts url.
     *
     * @return Accounts url.
     */
    public String getAccountsUrl() {
        return this.accountsUrl;
    }

    /**
     * Returns the balance of the user account.
     *
     * @param user
     *            User url.
     * @return Current balance.
     * @throws AccountAccessException
     *             If the user account could not be locked or if it does not
     *             exist.
     */
    public int getBalance(final String user) throws AccountAccessException {
        final SortedSet<String> involvedAccounts = new TreeSet<>();
        involvedAccounts.add(user);
        this.lock(involvedAccounts);
        // we know the account exist, otherwise lock() would fail
        final int balance = this.accounts.get(user).getBalance();
        this.unlock(involvedAccounts);
        return balance;
    }

    /**
     * Returns the bank's id.
     *
     * @return Bank id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the transfer url.
     *
     * @return Transfer url.
     */
    public String getTransferUrl() {
        return this.transferUrl;
    }

    /**
     * Checks if the given user already has an account.
     *
     * @param user
     *            User url.
     * @return <code>true</code> if there is an account for the given user,
     *         <code>false</code> otherwise.
     */
    public boolean hasAccount(final String user) {
        return this.accounts.containsKey(user);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.accounts == null) ? 0 : this.accounts.hashCode());
        result = prime * result + ((this.accountsUrl == null) ? 0 : this.accountsUrl.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.transferUrl == null) ? 0 : this.transferUrl.hashCode());
        return result;
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
     *            Set containing the user urls of all accounts to be locked.
     * @throws AccountAccessException
     *             If a user account could not be locked or if it does not
     *             exist.
     */
    public void lock(final SortedSet<String> accountList) throws AccountAccessException {
        // use default timeout of 1 second
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
     *            Set containing the user urls of all accounts to be locked.
     * @param timeout
     *            Timeout amount.
     * @param unit
     *            Timeout time unit.
     * @throws AccountAccessException
     *             If a user account could not be locked or if it does not
     *             exist.
     */
    public void lock(final SortedSet<String> accountList, final long timeout, final TimeUnit unit)
            throws AccountAccessException {
        // we need this list to keep track of already locked accounts to unlock
        // them when encountering errors
        final List<String> locked = new ArrayList<>();
        // flag to perform cleanup only when required
        boolean cleanup = false;
        try {
            for (final String current : accountList) {
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
                for (final String current : locked) {
                    this.accounts.get(current).unlock();
                }
            }
        }
    }

    /**
     * Performs the given atomic operation.
     *
     * @param operation
     *            Action to be performed.
     * @throws AccountAccessException
     *             if the operation could not be performed. There where no
     *             changes if this exception is thrown.
     */
    public void performAtomicOperation(final AtomicOperation operation)
            throws AccountAccessException {
        if (!operation.getBank().equals(this)) {
            throw new AccountAccessException("The operation belong to another bank instance.");
        }
        final Account account = this.getAccount(operation.getAccount());
        switch (operation.getType()) {
            case DEPOSIT:
                account.deposit(operation.getAmount());
                break;
            case WITHDRAW:
                account.withdraw(operation.getAmount());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Sets the accounts url.
     *
     * @param url
     *            New accounts url.
     */
    public void setAccountsUrl(final String url) {
        this.accountsUrl = url;
    }

    /**
     * Sets the transfer url.
     *
     * @param url
     *            New transfer url.
     */
    public void setTransferUrl(final String url) {
        this.transferUrl = url;
    }

    /**
     * Unlocks all given accounts.
     *
     * @param accountList
     *            Set containing the user urls of all accounts to be locked.
     * @throws AccountAccessException
     *             If a user account does not exist.
     */
    public void unlock(final SortedSet<String> accountList) throws AccountAccessException {
        for (final String current : accountList) {
            final Account account = this.getAccount(current);
            // unlock existing accounts
            account.unlock();
        }
    }

    /**
     * Returns the account for the given user.
     *
     * @param user
     *            User name to load the account for.
     * @return user account.
     * @throws AccountAccessException
     *             if there is no account for the given user.
     */
    private Account getAccount(final String user) throws AccountAccessException {
        final Account account = this.accounts.get(user);
        // check if the account exists
        if (account == null) {
            throw new AccountAccessException("Account for user '" + user + "' does not exist.");
        }
        return account;
    }
}
