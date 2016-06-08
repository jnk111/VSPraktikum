package vs.gerriet.model.transaction;

import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.PlayerId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.model.Bank;
import vs.jonas.services.json.EventData;

/**
 * <p>
 * Class for atomic operations used within transaction.
 * </p>
 * <p>
 * Does not provide a overloaded clone method because no deep copy is required.
 * </p>
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class AtomicOperation implements Cloneable {

    /**
     * Type of atomic operations.
     *
     * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
     */
    public enum Type {
        /**
         * Withdraw operation type.
         */
        WITHDRAW,
        /**
         * Deposit operation type.
         */
        DEPOSIT;

        /**
         * Returns the complementary type.
         *
         * @return Complement.
         */
        public Type getComplement() {
            switch (this) {
                case DEPOSIT:
                    return WITHDRAW;
                case WITHDRAW:
                    return DEPOSIT;
                default:
                    return this;
            }
        }
    }

    /**
     * Amount for the operation.
     */
    private final int amount;
    /**
     * Account this operation is performed on.
     */
    private final AccountId account;
    /**
     * Type of this operation.
     */
    private Type type;
    /**
     * Bank instance this operation is performed on.
     */
    private final Bank bank;
    /**
     * Message for this operation.
     */
    private final String message;
    /**
     * Transfer for this operation.
     */
    private final TransferId transfer;

    /**
     * Creates a new atomic operation from the given arguments. This does not
     * perform any action.
     *
     * @param bank
     *            Bank this operation belongs to.
     * @param type
     *            Type of this operation.
     * @param account
     *            Account this operation belongs to.
     * @param amount
     *            Amount for this operation.
     * @param message
     *            Message for this operation.
     * @param transfer
     *            Transfer id of the transfer this operation belongs to.
     */
    AtomicOperation(final Bank bank, final Type type, final AccountId account, final int amount,
            final String message, final TransferId transfer) {
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.bank = bank;
        this.message = message;
        this.transfer = transfer;
    }

    /**
     * Returns the account this operation belongs to.
     *
     * @return User account.
     */
    public AccountId getAccount() {
        return this.account;
    }

    /**
     * Returns the amount for this operation.
     *
     * @return Operation amount.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Returns the bank this operation belongs to.
     *
     * @return Bank instance.
     */
    public Bank getBank() {
        return this.bank;
    }

    /**
     * Returns the message for this operation.
     *
     * @return Operation message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the transfer id for this operation.
     *
     * @return Transfer id.
     */
    public TransferId getTransfer() {
        return this.transfer;
    }

    /**
     * Returns the type of this operation.
     *
     * @return Operation type.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Returns an event data object for this operation.
     *
     * @return Event data object.
     */
    EventData getEventData() {
        return new EventData(this.bank.getGameId().getUri(), "TRANSFER", this.type.name(),
                this.message, this.transfer.getUri(),
                new PlayerId(this.bank.getGameId(), this.account.getBaseData()).getUri());
    }

    /**
     * Performs this atomic operation.
     *
     * @throws TransactionException
     *             If the action could not be performed. The bank's state did
     *             not change if this exception is thrown.
     * @see {@link Bank#performAtomicOperation(AtomicOperation)}
     */
    void perform() throws TransactionException {
        try {
            this.getBank().performAtomicOperation(this);
        } catch (final AccountAccessException ex) {
            throw new TransactionException(ex);
        }
    }

    /**
     * Undoes this atomic operation.
     *
     * @throws TransactionException
     *             If the action could not be performed. The bank's state did
     *             not change if this exception is thrown.
     * @see
     */
    void undo() throws TransactionException {
        try {
            final AtomicOperation op = (AtomicOperation) this.clone();
            op.type = this.type.getComplement();
            this.getBank().performAtomicOperation(this);
        } catch (final AccountAccessException ex) {
            throw new TransactionException(ex);
        } catch (final CloneNotSupportedException ex) {
            // should never occur
            throw new TransactionException("Gerriet hat in der Implementierung Mist gebaut!", ex);
        }
    }
}
