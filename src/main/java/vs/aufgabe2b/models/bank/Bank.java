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

/**
 * Thread save bank with transaction support.
 * 
 * Account interaction is done by using locks on per account basis to allow
 * multiple interactions that don't influence each other at the same time.
 * 
 * @author Gerriet Hinrichs <gerriet.hinrichs@web.de>
 */
public class Bank {

	/**
	 * We use a sorted map to store accounts to ensure proper ordering. This is
	 * required since we can only lock one account at a time and always want to
	 * lock / unlock accounts in the same order to prevent deadlocks.
	 */
	private SortedMap<String, Account> accounts;

	/**
	 * Creates a new bank without any accounts.
	 */
	public Bank() {
		// create an account map and add synchronization
		this.accounts = Collections.synchronizedSortedMap(new TreeMap<String, Account>());
	}

	/**
	 * Locks the given accounts. If <code>false</code> is returned none of the
	 * given accounts where locked. This method uses
	 * {@link #lock(SortedSet, long, TimeUnit)} with a timeout of one second.
	 * 
	 * We have the {@link SortedSet} type here to prevent locking the same
	 * account twice which would result in a deadlock.
	 * 
	 * @param accounts
	 *            Set containing the user urls of all accounts to be locked.
	 * @throws AccountAccessException
	 *             If a user account could not be locked or if it does not
	 *             exist.
	 */
	private void lock(SortedSet<String> accounts) throws AccountAccessException {
		// use default timeout of 1 second
		this.lock(accounts, 1, TimeUnit.SECONDS);
	}

	/**
	 * Locks the given accounts. If <code>false</code> is returned none of the
	 * given accounts where locked. Timeout is used for each specified account.
	 * The total timeout will be the given timeout multiplied by the number of
	 * accounts to be locked.
	 * 
	 * We have the {@link SortedSet} type here to prevent locking the same
	 * account twice which would result in a deadlock.
	 * 
	 * @param accounts
	 *            Set containing the user urls of all accounts to be locked.
	 * @param timeout
	 *            Timeout amount.
	 * @param unit
	 *            Timeout time unit.
	 * @throws AccountAccessException
	 *             If a user account could not be locked or if it does not
	 *             exist.
	 */
	private void lock(SortedSet<String> accounts, long timeout, TimeUnit unit) throws AccountAccessException {
		// we need this list to keep track of already locked accounts to unlock
		// them when encountering errors
		List<String> locked = new ArrayList<String>();
		// flag to perform cleanup only when required
		boolean cleanup = false;
		try {
			for (String current : accounts) {
				Account account = this.accounts.get(current);
				// check if the account exists
				if (account == null) {
					throw new AccountAccessException("Account for user '" + current + "' does not exist.");
				}
				// lock existing accounts
				if (account.lock(timeout, unit)) {
					locked.add(current);
				} else {
					throw new AccountAccessException("Failed to lock account for user '" + current + "'");
				}
			}
		} catch (InterruptedException e) {
			// set flag for cleanup
			cleanup = true;
			// create account access exception
			throw new AccountAccessException(e);
		} catch (AccountAccessException e) {
			// set flag for cleanup
			cleanup = true;
			// re-throw exception
			throw e;
		} finally {
			// perform cleanup if the operation was not successful
			if (!cleanup) {
				// unlock all accounts we locked before the error
				for (String current : locked) {
					this.accounts.get(current).unlock();
				}
			}
		}
	}

	/**
	 * Unlocks all given accounts.
	 * 
	 * @param accounts
	 *            Set containing the user urls of all accounts to be locked.
	 * @throws AccountAccessException
	 *             If a user account does not exist.
	 */
	private void unlock(SortedSet<String> accounts) throws AccountAccessException {
		for (String current : accounts) {
			Account account = this.accounts.get(current);
			// unlock existing accounts
			if (account != null) {
				account.unlock();
			} else {
				throw new AccountAccessException("Account for user '" + current + "' does not exist.");
			}
		}
	}

	/**
	 * Checks if the given user already has an account.
	 * 
	 * @param user
	 *            User url.
	 * @return <code>true</code> if there is an account for the given user,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasAccount(String user) {
		return this.accounts.containsKey(user);
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
	public boolean createAccount(String user) {
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
	public synchronized boolean createAccount(String user, int balance) {
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
	public void deleteAccount(String user) {
		this.accounts.remove(user);
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
	public int getBalance(String user) throws AccountAccessException {
		SortedSet<String> involvedAccounts = new TreeSet<String>();
		involvedAccounts.add(user);
		this.lock(involvedAccounts);
		// we know the account exist, otherwise lock() would fail
		int balance = this.accounts.get(user).getBalance();
		this.unlock(involvedAccounts);
		return balance;
	}

	/**
	 * Deposits the given amount to the user account.
	 * 
	 * @param user
	 *            User url.
	 * @param amount
	 *            Amount to be added.
	 * @return New account balance.
	 * @throws AccountAccessException
	 *             If the user account could not be locked or if it does not
	 *             exist.
	 */
	public int deposit(String user, int amount) throws AccountAccessException {
		SortedSet<String> involvedAccounts = new TreeSet<String>();
		involvedAccounts.add(user);
		this.lock(involvedAccounts);
		int balance = this.accounts.get(user).deposit(amount);
		this.unlock(involvedAccounts);
		return balance;
	}

	/**
	 * Withdraws the given amount from the user account.
	 * 
	 * @param user
	 *            User url.
	 * @param amount
	 *            Amount to be withdrawn.
	 * @return New account balance.
	 * @throws AccountAccessException
	 *             If the user account could not be locked or if it does not
	 *             exist.
	 */
	public int withdraw(String user, int amount) throws AccountAccessException {
		SortedSet<String> involvedAccounts = new TreeSet<String>();
		involvedAccounts.add(user);
		this.lock(involvedAccounts);
		int balance = this.accounts.get(user).withdraw(amount);
		this.unlock(involvedAccounts);
		return balance;
	}

	/**
	 * Transfers the given amount from one account to the other.
	 * 
	 * @param from
	 *            User url of the account sending the amount.
	 * @param to
	 *            User url of the account receiving the amount.
	 * @param amount
	 *            Amount to be transferred.
	 * @throws AccountAccessException
	 *             If a user account could not be locked or if it does not
	 *             exist.
	 */
	public void transfer(String from, String to, int amount) throws AccountAccessException {
		SortedSet<String> involvedAccounts = new TreeSet<String>();
		involvedAccounts.add(from);
		involvedAccounts.add(to);
		this.lock(involvedAccounts);
		this.accounts.get(from).withdraw(amount);
		this.accounts.get(to).deposit(amount);
		this.unlock(involvedAccounts);
	}
}
