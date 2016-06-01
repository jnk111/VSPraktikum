package vs.gerriet.json;

import vs.gerriet.model.transaction.Transaction.Status;

/**
 * JSON data object for transaction status.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransactionInfo {
    /**
     * Transaction type.
     */
    public String type;
    /**
     * Transfer uri.
     */
    public String uri;

    /**
     * Transaction status.
     *
     * @see Status#name()
     * @see Status#fromName(String)
     */
    public String status;

    /**
     * Creates a new transaction info instance.
     *
     * @param type
     *            Transaction type.
     * @param uri
     *            Transaction uri.
     */
    public TransactionInfo(final String type, final String uri, final String status) {
        this.type = type;
        this.uri = uri;
        this.status = status;
    }
}
