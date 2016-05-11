package vs.gerriet.transaction;

import java.util.ArrayList;
import java.util.List;

import vs.gerriet.json.TransferInfo;
import vs.gerriet.model.Bank;
import vs.gerriet.transaction.AtomicOperation.Type;
import vs.gerriet.utils.IdUtils;

public class Transfer {

    final List<AtomicOperation> operations = new ArrayList<>(2);

    private final String from;
    private final String to;
    private final int amount;
    private final String reason;
    private final String id;

    public Transfer(final Bank bank, final String from, final String to, final int amount,
            final String reason) {
        this.operations.add(new AtomicOperation(bank, Type.WITHDRAW, from, amount, reason));
        this.operations.add(new AtomicOperation(bank, Type.DEPOSIT, to, amount, reason));
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.reason = reason;
        this.id = Long.toString(IdUtils.getUniqueRunntimeId());
    }

    public Transfer(final Bank bank, final Type type, final String player, final int amount,
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
        this.id = Long.toString(IdUtils.getUniqueRunntimeId());
    }

    public String getId() {
        return this.id;
    }

    public TransferInfo getInfo() {
        return new TransferInfo(this.from, this.to, this.amount, this.reason);
    }
}
