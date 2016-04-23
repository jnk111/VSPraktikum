package vs.aufgabe2b.models.bank;

import vs.aufgabe2b.utils.LockProvider;

/**
 * Thread save class for user accounts with locking.
 * 
 * @author Gerriet Hinrichs <gerriet.hinrichs@web.de>
 */
public class Account extends LockProvider {

	/**
	 * Contains the user url this account belongs to.
	 */
	private final String user;

	/**
	 * Account balance.
	 */
	// TODO @gerriet-hinrichs: check if double is needed.
	private int balance;

	/**
	 * Creates a new account instance with locking support. The account will
	 * have balance <code>0</code>.
	 * 
	 * @param user
	 *            User url.
	 */
	Account(String user) {
		this.user = user;
		this.balance = 0;
	}

	/**
	 * Returns the user for this account.
	 * 
	 * @return User url.
	 */
	String getUser() {
		return this.user;
	}

	/**
	 * Creates a new account instance with locking support. The account will
	 * have the given balance.
	 * 
	 * @param user
	 *            User url.
	 * @param balance
	 *            Account balance.
	 */
	Account(String user, int balance) {
		this.user = user;
		this.balance = balance;
	}

	/**
	 * Withdraws the specified amount from this account.
	 * 
	 * @param amount
	 *            Amount to be withdrawn from this account.
	 * @return New account balance.
	 */
	synchronized int withdraw(int amount) {
		return this.balance -= amount;
	}

	/**
	 * Deposits the specified amount to this account.
	 * 
	 * @param amount
	 *            Amount to be added.
	 * @return New account balance.
	 */
	synchronized int deposit(int amount) {
		return this.balance += amount;
	}

	/**
	 * Returns the account's balance.
	 * 
	 * @return Account balance.
	 */
	synchronized int getBalance() {
		return this.balance;
	}

	/**
	 * Sets the account's balance.
	 * 
	 * @param balance
	 *            New balance for the account.
	 */
	synchronized void setBalance(int balance) {
		this.balance = balance;
	}
}
