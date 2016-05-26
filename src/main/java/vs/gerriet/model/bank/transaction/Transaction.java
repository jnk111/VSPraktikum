package vs.gerriet.model.bank.transaction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.SortedSet;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.model.bank.Bank;
import vs.gerriet.utils.LockProvider;

/**
 * Class for bank transactions.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class Transaction extends LockProvider {
    /**
     * Contains transaction status types.
     *
     * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
     */
    public enum Status {
        /**
         * Default status, operations can be added.
         */
        ACTIVE,
        /**
         * There was at least one confirmation, waiting for others. If an
         * operation is added, returns to active state.
         */
        WAITING_FOR_CONFIRMATION,
        /**
         * Confirmation completed. If an operation is added, returns to active
         * state.
         */
        CONFIRMED,
        /**
         * {@link Transaction#commit()} or {@link Transaction#rollback()} is
         * currently running.
         */
        RUNNING,
        /**
         * The state of this transaction is invalid and
         * {@link Transaction#commit()} will always fail.
         */
        INVALID,
        /**
         * This transaction was committed.
         */
        COMMITTED,
        /**
         * This transaction was rolled back.
         */
        ROLLED_BACK;

        /**
         * Transforms the given status enum name into the matching enum element.
         *
         * @param name
         *            Status enum name.
         * @return Matching enum element.
         */
        public static Status fromName(final String name) {
            if (name.equals(ACTIVE.name())) {
                return ACTIVE;
            }
            if (name.equals(WAITING_FOR_CONFIRMATION.name())) {
                return WAITING_FOR_CONFIRMATION;
            }
            if (name.equals(CONFIRMED.name())) {
                return CONFIRMED;
            }
            if (name.equals(RUNNING.name())) {
                return RUNNING;
            }
            if (name.equals(COMMITTED.name())) {
                return Status.COMMITTED;
            }
            if (name.equals(ROLLED_BACK.name())) {
                return Status.ROLLED_BACK;
            }
            return INVALID;
        }
    }

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
     * Status flag. If <code>null</code>, the status is either
     * {@link Status#ACTIVE}, {@value Status#WAITING_FOR_CONFIRMATION} or
     * {@value Status#CONFIRMED}.
     */
    private Status status;

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
    private final SortedSet<AccountId> accounts;

    /**
     * Contains confirmation status for accounts.
     */
    private final SortedSet<AccountId> confirmed;

    /**
     * List containing all transfers for this transaction.
     */
    private final List<Transfer> transfers;

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
        this.accounts = new ConcurrentSkipListSet<>();
        this.confirmed = new ConcurrentSkipListSet<>();
        this.transfers = Collections.synchronizedList(new LinkedList<>());
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
        if (this.isOperationValid(operation) && this.operations.offer(operation)) {
            this.accounts.add(operation.getAccount());
            this.confirmed.clear();
            return true;
        }
        return false;
    }

    /**
     * Adds the given transfer to this transaction's internal queue. This method
     * checks the bank instance assigned to the operation(s) and will not add
     * the element if this transaction is locked.
     *
     * @param operation
     *            Transfer to be added.
     * @return <code>true</code> if the transfer was added, <code>false</code>
     *         otherwise.
     */
    public synchronized boolean addOperation(final Transfer operation) {
        for (final AtomicOperation op : operation.operations) {
            if (!this.isOperationValid(op)) {
                return false;
            }
        }
        boolean res = true;
        for (final AtomicOperation op : operation.operations) {
            res = res && this.addOperation(op);
        }
        if (!res) {
            this.operations.removeAll(operation.operations);
        }
        this.transfers.add(operation);
        return res;
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
        final Status prevStatus = this.getStatus();
        this.status = Status.RUNNING;
        // check transaction status
        switch (prevStatus) {
            case CONFIRMED:
                break;
            case ACTIVE:
                if (this.type == Type.SIMPLE) {
                    break;
                }
                return false;
            default:
                return false;
        }
        // lock this instance and all user accounts first
        try {
            this.lock();
            this.bank.getAccounts().lock(this.accounts);
        } catch (@SuppressWarnings("unused") InterruptedException | AccountAccessException ex) {
            // locking this transaction failed or at least one account we
            // processed does not exist
            this.unlock();
            this.status = Status.INVALID;
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
                this.bank.getAccounts().unlock(this.accounts);
            } catch (final AccountAccessException ex) {
                // should not occur, we already locked all accounts. If we go
                // here something went horribly wrong!
                throw new TransactionException(ex);
            } finally {
                this.unlock();
            }
        }
        // if everything is successful
        this.addTransfersToBank();
        this.createEvents();
        this.status = Status.COMMITTED;
        return true;
    }

    /**
     * Confirms this transaction for the given account.
     *
     * @param account
     *            Account to confirm this transaction for.
     */
    public void confirm(final AccountId account) {
        this.confirmed.add(account);
    }

    /**
     * Returns the status for this transaction.
     *
     * @return Transaction status.
     */
    public Status getStatus() {
        if (this.status != null) {
            return this.status;
        }
        if (this.isConfirmed()) {
            if (this.type == Type.CHECKED) {
                return Status.CONFIRMED;
            }
            return Status.ACTIVE;
        }
        if (this.confirmed.isEmpty()) {
            return Status.ACTIVE;
        }
        return Status.WAITING_FOR_CONFIRMATION;
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
        final Status prevStatus = this.getStatus();
        // check status
        switch (prevStatus) {
            case COMMITTED:
            case INVALID:
            case ROLLED_BACK:
                return;
            default:
                break;
        }
        this.status = Status.RUNNING;
        while (!this.doneOperations.isEmpty()) {
            this.doneOperations.pop().undo();
        }
        this.status = Status.ROLLED_BACK;
    }

    /**
     * Adds all transfers from this transaction to the bank.
     */
    private void addTransfersToBank() {
        this.transfers.forEach(transfer -> {
            this.bank.addTransfer(transfer);
        });
    }

    /**
     * Creates events for all done operations.
     */
    private void createEvents() {
        while (!this.doneOperations.isEmpty()) {
            this.doneOperations.pop().createEvent();
        }
    }

    /**
     * Checks if the given operation is valid.
     *
     * @param operation
     *            Operation to check.
     * @return <code>true</code> if it's valid, <code>false</code> otherwise.
     */
    private boolean isOperationValid(final AtomicOperation operation) {
        return !this.isLocked() && operation.getBank().equals(this.bank);
    }
}
