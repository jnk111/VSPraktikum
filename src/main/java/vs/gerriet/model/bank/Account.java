package vs.gerriet.model.bank;

import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.id.PlayerId;
import vs.gerriet.utils.LockProvider;

/**
 * Thread save class for user accounts with locking.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class Account extends LockProvider {

    /**
     * Contains the user id this account belongs to.
     */
    private final PlayerId user;

    /**
     * Account balance.
     */
    private int balance;

    /**
     * Creates a new account instance with locking support. The account will
     * have balance <code>0</code>.
     *
     * @param user
     *            User id.
     */
    Account(final PlayerId user) {
        this.user = user;
        this.balance = 0;
    }

    /**
     * Creates a new account instance with locking support. The account will
     * have the given balance.
     *
     * @param user
     *            User id.
     * @param balance
     *            Account balance.
     */
    Account(final PlayerId user, final int balance) {
        this.user = user;
        this.balance = balance;
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
        final Account other = (Account) obj;
        if (this.balance != other.balance) {
            return false;
        }
        if (this.user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!this.user.equals(other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.balance;
        result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
        return result;
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
     * @return User Id.
     */
    PlayerId getUser() {
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
     * @throws AccountAccessException
     *             if there is not enough money on this account.
     */
    synchronized int withdraw(final int amount) throws AccountAccessException {
        if (this.balance - amount < 0) {
            throw new AccountAccessException("Unsufficiant fonds for user '" + this.user + "'");
        }
        return this.balance -= amount;
    }
}
