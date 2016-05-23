package vs.gerriet.id.bank;

import spark.Request;
import vs.gerriet.id.BankId;
import vs.gerriet.id.Id;

/**
 * Transfer id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransferId extends Id<Integer> {

    /**
     * Bank this transfer is associated to.
     */
    private BankId bank;

    /**
     * Creates a new transfer id container.
     *
     * @param bank
     *            Bank this transfer is associated to.
     * @param data
     *            Contained id.
     */
    public TransferId(final BankId bank, final Integer data) {
        super(data);
        this.bank = bank;
    }

    /**
     * Creates a new transfer id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public TransferId(final BankId bank, final Request request, final String param) {
        super(request, param);
        this.bank = bank;
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
        final TransferId other = (TransferId) obj;
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
     * Returns the bank associated to this transfer.
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
     * Sets the bank for this transfer.
     *
     * @param bank
     *            New bank id.
     */
    public void setBank(final BankId bank) {
        this.bank = bank;
    }

    @Override
    protected Integer fromUriSuffix(final String suffix) {
        return Integer.getInteger(suffix);
    }

    @Override
    protected String getUriPrefix() {
        return this.bank.getUri();
    }
}