package vs.gerriet.json;

/**
 * Object for the bank list.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class BankList {

    /**
     * Bank uri list.
     */
    public String[] banks;

    /**
     * Creates a new response with the given bank uri list.
     *
     * @param banks
     *            Bank uri list.
     */
    public BankList(final String[] banks) {
        this.banks = banks;
    }
}
