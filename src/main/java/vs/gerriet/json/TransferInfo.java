package vs.gerriet.json;

/**
 * JSON data object for transfers.
 * 
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransferInfo {
    /**
     * Transfer source uri.
     */
    public String from;
    /**
     * Transfer target uri.
     */
    public String to;
    /**
     * Transfer amount.
     */
    public int amount;
    /**
     * Transfer reason.
     */
    public String reason;

    /**
     * Creates a new transfer info instance.
     * 
     * @param from
     *            Transfer source uri.
     * @param to
     *            Transfer target uri.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     */
    public TransferInfo(final String from, final String to, final int amount, final String reason) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.reason = reason;
    }
}
