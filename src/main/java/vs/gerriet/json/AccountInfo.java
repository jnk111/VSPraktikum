package vs.gerriet.json;

/**
 * Account info JSON object.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class AccountInfo {

    /**
     * Player uri.
     */
    public String player;
    /**
     * Account balance.
     */
    public int balance;

    /**
     * Account uri.
     */
    public String uri;

    /**
     * Creates a new instance.
     *
     * @param player
     *            Player uri.
     * @param balance
     *            Account balance.
     */
    public AccountInfo(final String player, final int balance, final String uri) {
        this.balance = balance;
        this.player = player;
        this.uri = uri;
    }
}
