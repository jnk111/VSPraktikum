package vs.gerriet.id.bank;

import spark.Request;
import vs.gerriet.id.BankId;
import vs.gerriet.id.Id;

/**
 * Transaction id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransactionId extends Id<Integer> {

    /**
     * Bank this transaction is associated to.
     */
    private BankId bank;

    /**
     * Creates a new transaction id container.
     *
     * @param bank
     *            Bank this transaction is associated to.
     * @param data
     *            Contained id.
     */
    public TransactionId(final BankId bank, final Integer data) {
        super(data);
        this.bank = bank;
    }

    /**
     * Creates a new transaction id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public TransactionId(final BankId bank, final Request request, final String param) {
        super(request, param);
        this.bank = bank;
    }

    @Override
    public int compareTo(final Id<Integer> obj) {
        if (obj instanceof TransactionId) {
            final TransactionId other = (TransactionId) obj;
            if (this.bank != null) {
                // compare bank first
                final int compare = this.bank.compareTo(other.bank);
                if (compare != 0) {
                    return compare;
                }
            } else {
                if (other.bank != null) {
                    // other instance has bank set: other is larger
                    return 1;
                }
                // both bank field null: compare data
            }
            return super.compareTo(obj);
        }
        // invalid case...
        throw new UnsupportedOperationException("Cannot compare " + this.getClass().getName()
                + " with " + obj.getClass().getName());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final TransactionId other = (TransactionId) obj;
        if (this.bank == null) {
            if (other.bank != null) {
                return false;
            }
        } else if (!this.bank.equals(other.bank)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the bank for this transaction.
     *
     * @return Bank id.
     */
    public BankId getBank() {
        return this.bank;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.bank == null) ? 0 : this.bank.hashCode());
        return result;
    }

    /**
     * Sets the bank for this transaction.
     *
     * @param bank
     *            New bank id.
     */
    public void setBank(final BankId bank) {
        this.bank = bank;
    }

    @Override
    protected Integer fromUriSuffix(final String suffix) {
        return Integer.decode(suffix);
    }

    @Override
    protected String getUriPrefix() {
        return this.bank.getUri() + "/transaction/";
    }
}
