package vs.gerriet.json.bank;

/**
 * Object for the account list.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class AccountList {
    /**
     * Account uri list.
     */
    public String[] accounts;

    /**
     * Creates a new response with the given account uri list.
     *
     * @param accounts
     *            Transfer uri list.
     */
    public AccountList(final String[] accounts) {
        this.accounts = accounts;
    }
}
