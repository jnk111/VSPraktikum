package vs.gerriet.json;

/**
 * Object for the transfer list.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransferList {
    /**
     * Transfer uri list.
     */
    public String[] transfers;

    /**
     * Creates a new response with the given transfer uri list.
     *
     * @param transfers
     *            Transfer uri list.
     */
    public TransferList(final String[] transfers) {
        this.transfers = transfers;
    }
}
