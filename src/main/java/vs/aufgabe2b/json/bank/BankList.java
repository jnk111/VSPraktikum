package vs.aufgabe2b.json.bank;

/**
 * Object for the bank list.
 *
 * @author Gerriet Hinrichs <gerriet.hinrichs@web.de>
 */
public class BankList {

    /**
     * Bank id list.
     */
    public String[] banks;

    /**
     * Creates a new response with the given bank id list.
     *
     * @param banks
     *            Bank id list.
     */
    public BankList(final String[] banks) {
	this.banks = banks;
    }
}
