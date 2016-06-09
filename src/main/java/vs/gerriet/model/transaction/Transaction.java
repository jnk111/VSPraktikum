package vs.gerriet.model.transaction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.SortedSet;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import vs.gerriet.event.AddEventQueue;
import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.BankId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.json.TransactionInfo;
import vs.gerriet.model.Bank;
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
     * Event queue used to create events.
     */
    private final AddEventQueue eventQueue;
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
     * Contains the id of this transaction.
     */
    private final TransactionId id;
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
    public Transaction(final TransactionId id, final Type type, final Bank bank,
            final AddEventQueue eventQueue) {
        this.id = id;
        this.type = type;
        this.bank = bank;
        this.operations = new ConcurrentLinkedQueue<>();
        // stack is based on Vector which is thread safe
        this.doneOperations = new Stack<>();
        this.accounts = new ConcurrentSkipListSet<>();
        this.confirmed = new ConcurrentSkipListSet<>();
        this.transfers = Collections.synchronizedList(new LinkedList<>());
        this.eventQueue = eventQueue;
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
        boolean res = true;
        for (final AtomicOperation op : operation.operations) {
            if (op != null) {
                if (this.isOperationValid(op)) {
                    if (!this.addOperation(op)) {
                        res = false;
                        break;
                    }
                } else {
                    res = false;
                    break;
                }
            }
        }
        if (!res) {
            this.operations.removeAll(operation.operations);
            // set failed state to transaction
            operation.failed = true;
            operation.pending = false;
        } else {
            this.bank.addTransfer(operation);
            this.transfers.add(operation);
        }
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
        this.setTransferSuccess();
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
     * Returns the id of this Transaction.
     *
     * @return Transaction id.
     */
    public TransactionId getId() {
        return this.id;
    }

    /**
     * Returns info about the given transaction.
     *
     * @return Transaction info.
     */
    public TransactionInfo getInfo() {
        String typeString = "";
        switch (this.type) {
            case CHECKED:
                typeString = "2-phase";
                break;
            case SIMPLE:
            default:
                typeString = "1-phase";
                break;
        }
        return new TransactionInfo(typeString, this.id.getUri(), this.getStatus().name());
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
        this.setTransferFailure();
        this.status = Status.ROLLED_BACK;
    }

    /**
     * Creates events for all done operations.
     */
    private void createEvents() {
        // fill event queue with events from this transaction
        while (!this.doneOperations.isEmpty()) {
            this.eventQueue.push(this.doneOperations.pop().getEventData());
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
        if (this.isLocked()) {
            return false;
        }
        final BankId id1 = this.bank.getId();
        final BankId id2 = operation.getBank().getId();
        if (id1.equals(id2)) {
            return true;
        }
        return false;
    }

    /**
     * Updates the transfer instances within the bank to all have failed state.
     */
    private void setTransferFailure() {
        this.transfers.forEach(transfer -> {
            transfer.failed = true;
            transfer.pending = false;
            // also update transfer within bank (reference somehow does not
            // work)
            this.bank.addTransfer(transfer);
        });
    }

    /**
     * Updates the transfer instances within the bank to all have success state.
     */
    private void setTransferSuccess() {
        this.transfers.forEach(transfer -> {
            transfer.failed = false;
            transfer.pending = false;
            // also update transfer within bank (reference somehow does not
            // work)
            this.bank.addTransfer(transfer);
        });
    }
}
