package vs.gerriet.json;

/**
 * Object for the transaction list.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransactionList {
    /**
     * Transaction uri list.
     */
    public String[] transactions;

    /**
     * Creates a new response with the given transaction uri list.
     *
     * @param transactions
     *            Transfer uri list.
     */
    public TransactionList(final String[] transactions) {
        this.transactions = transactions;
    }
}
