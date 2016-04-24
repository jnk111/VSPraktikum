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
    // TODO @gerriet-hinrichs: check if double or long is needed.
    private int balance;

    /**
     * Creates a new account instance with locking support. The account will
     * have balance <code>0</code>.
     *
     * @param user
     *            User url.
     */
    Account(final String user) {
	this.user = user;
	this.balance = 0;
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
    Account(final String user, final int balance) {
	this.user = user;
	this.balance = balance;
    }

    /**
     * Deposits the specified amount to this account.
     *
     * @param amount
     *            Amount to be added.
     * @return New account balance.
     */
    synchronized int deposit(final int amount) {
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
     * Returns the user for this account.
     *
     * @return User url.
     */
    String getUser() {
	return this.user;
    }

    /**
     * Sets the account's balance.
     *
     * @param balance
     *            New balance for the account.
     */
    synchronized void setBalance(final int balance) {
	this.balance = balance;
    }

    /**
     * Withdraws the specified amount from this account.
     *
     * @param amount
     *            Amount to be withdrawn from this account.
     * @return New account balance.
     */
    synchronized int withdraw(final int amount) {
	return this.balance -= amount;
    }
}
