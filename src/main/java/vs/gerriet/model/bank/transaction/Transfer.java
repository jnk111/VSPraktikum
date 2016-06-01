package vs.gerriet.model.bank.transaction;

import java.util.ArrayList;
import java.util.List;

import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.TransferInfo;
import vs.gerriet.model.bank.Bank;
import vs.gerriet.model.bank.transaction.AtomicOperation.Type;
import vs.gerriet.utils.IdUtils;

/**
 * Class for transfers.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Transfer {

    /**
     * Atomic operations from this transfer.
     */
    final List<AtomicOperation> operations = new ArrayList<>(2);

    /**
     * Account used for withdraw.
     */
    private final AccountId from;
    /**
     * Account used for deposit.
     */
    private final AccountId to;
    /**
     * Transfer amount.
     */
    private final int amount;
    /**
     * Transfer reason.
     */
    private final String reason;
    /**
     * Transfer id (application unique).
     */
    private final TransferId id;
    /**
     * Transfer failed status.
     */
    boolean failed = false;
    /**
     * Transfer pending status.
     */
    boolean pending = true;

    /**
     * Creates a new transfer from one account to an other.
     *
     * @param bank
     *            Bank instance to perform the transfer on.
     * @param from
     *            Account to transfer the amount from.
     * @param to
     *            Account to transfer the amount to.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     */
    public Transfer(final Bank bank, final AccountId from, final AccountId to, final int amount,
            final String reason) {
        this.id = new TransferId(bank.getId(), Integer.valueOf(IdUtils.getUniqueRunntimeId()));
        this.operations
                .add(new AtomicOperation(bank, Type.WITHDRAW, from, amount, reason, this.id));
        this.operations.add(new AtomicOperation(bank, Type.DEPOSIT, to, amount, reason, this.id));
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.reason = reason;
    }

    /**
     * Creates a new transfer with the bank itself.
     *
     * @param bank
     *            Bank instance to perform the transfer on.
     * @param type
     *            Type of this transfer.
     * @param player
     *            Account to perform the transfer on.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     */
    public Transfer(final Bank bank, final Type type, final AccountId player, final int amount,
            final String reason) {
        this.operations.add(new AtomicOperation(bank, type, player, amount, reason));
        this.reason = reason;

        switch (type) {
            case DEPOSIT:
                this.to = player;
                this.from = null;
                break;
            case WITHDRAW:
                this.from = player;
                this.to = null;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        this.amount = amount;
        this.id = new TransferId(bank.getId(), Integer.valueOf(IdUtils.getUniqueRunntimeId()));
    }

    /**
     * Returns the transfer id.
     *
     * @return Transfer id.
     */
    public TransferId getId() {
        return this.id;
    }

    /**
     * Returns all information about this transfer.
     *
     * @return Transfer info.
     */
    public TransferInfo getInfo() {
        return new TransferInfo(this.from == null ? null : this.from.getUri(),
                this.to == null ? null : this.to.getUri(), this.amount, this.reason, this.failed,
                this.pending);
    }
}
