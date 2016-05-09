package vs.aufgabe2b.transaction;

import java.util.Collections;
import java.util.Queue;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import vs.aufgabe2b.exceptions.bank.AccountAccessException;
import vs.aufgabe2b.exceptions.bank.TransactionException;
import vs.aufgabe2b.models.bank.Bank;
import vs.aufgabe2b.utils.LockProvider;

/**
 * Class for bank transactions.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class Transaction extends LockProvider {
    /**
     * Contains transaction types.
     *
     * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
     */
    public enum Type {
        /**
         * Simple transactions that are not required to be validated.
         */
        SIMPLE,
        /**
         * Transaction that needs to be validated by all users that participate.
         */
        CHECKED;
    }

    /**
     * Contains the transaction type.
     */
    private final Type type;

    /**
     * <p>
     * Queue containing all operations from this transaction.
     * </p>
     * <p>
     * The queue is used so operations are performed in order.
     * </p>
     */
    private final Queue<AtomicOperation> operations;

    /**
     * <p>
     * Stack containing already done operations used for roll back.
     * </p>
     * <p>
     * A stack is used so operations are rolled back in reverse order.
     * </p>
     */
    private final Stack<AtomicOperation> doneOperations;

    /**
     * We use a sorted map to store accounts to ensure proper ordering. This is
     * required since we can only lock one account at a time and always want to
     * lock / unlock accounts in the same order to prevent deadlocks.
     */
    private final SortedSet<String> accounts;

    /**
     * Contains confirmation status for accounts.
     */
    private final SortedSet<String> confirmed;
    /**
     * Contains the bank instance for this transaction. This is required so only
     * operations to be performed on that bank can be added.
     */
    private final Bank bank;

    /**
     * Creates a new transaction.
     *
     * @param type
     *            Transaction type.
     * @param bank
     *            Bank instance for this transaction.
     */
    public Transaction(final Type type, final Bank bank) {
        this.type = type;
        this.bank = bank;
        this.operations = new ConcurrentLinkedQueue<>();
        // stack is based on Vector which is thread safe
        this.doneOperations = new Stack<>();
        this.accounts = Collections.synchronizedSortedSet(new TreeSet<String>());
        this.confirmed = Collections.synchronizedSortedSet(new TreeSet<String>());
    }

    /**
     * Adds the given operation to this transaction's internal queue. This
     * method checks the bank instance assigned to the operation and will not
     * add the element if this transaction is locked.
     *
     * @param operation
     *            Operation to be added.
     * @return <code>true</code> if the element was added, <code>false</code>
     *         otherwise.
     */
    public synchronized boolean addOperation(final AtomicOperation operation) {
        if (!this.isLocked() && operation.getBank().equals(this.bank)
                && this.operations.offer(operation)) {
            this.accounts.add(operation.getAccount());
            this.confirmed.clear();
            return true;
        }
        return false;
    }

    /**
     * Commits this transaction and performs all queued operations.
     *
     * @return <code>true</code> if all operations where performed without
     *         errors, <code>false</code> otherwise. If <code>false</code> is
     *         returned the bank's state is not changed.
     *         <p>
     *         This method will always return <code>false</code> if
     *         {@link #isConfirmed()} returns <code>false</code>.
     *         </p>
     * @throws TransactionException
     *             If something went horribly wrong.
     */
    public synchronized boolean commit() throws TransactionException {
        // check if the transaction is confirmed
        if (!this.isConfirmed()) {
            return false;
        }
        // lock this instance and all user accounts first
        try {
            this.lock();
            this.bank.lock(this.accounts);
        } catch (@SuppressWarnings("unused") InterruptedException | AccountAccessException ex) {
            // locking this transaction failed or at least one account we
            // processed does not exist
            this.unlock();
            return false;
        }
        // perform operations and keep track of all successful ones
        try {
            AtomicOperation current;
            while ((current = this.operations.poll()) != null) {
                current.perform();
                this.doneOperations.push(current);
            }
        } catch (@SuppressWarnings("unused") final TransactionException ex) {
            // one operation failed (during transaction)
            this.rollback();
            return false;
        } finally {
            // unlock all user accounts
            try {
                this.bank.unlock(this.accounts);
            } catch (final AccountAccessException ex) {
                // should not occur, we already locked all accounts. If we go
                // here something went horribly wrong!
                throw new TransactionException(ex);
            } finally {
                this.unlock();
            }
        }
        return true;
    }

    /**
     * Confirms this transaction for the given account.
     *
     * @param account
     *            Account to confirm this transaction for.
     */
    public void confirm(final String account) {
        this.confirmed.add(account);
    }

    /**
     * Returns this transaction's type.
     *
     * @return Transaction type.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Returns the confirmation status for this transaction.
     *
     * @return <code>true</code> if this transaction is either of type
     *         {@link Type#SIMPLE} or if all accounts participating in this
     *         transaction have confirmed it.
     */
    public boolean isConfirmed() {
        return this.type == Type.SIMPLE || this.confirmed.containsAll(this.accounts);
    }

    /**
     * Rolls this transaction back.
     *
     * @throws TransactionException
     *             If something went horribly wrong.
     */
    public synchronized void rollback() throws TransactionException {
        while (!this.doneOperations.isEmpty()) {
            this.doneOperations.pop().undo();
        }
    }
}
