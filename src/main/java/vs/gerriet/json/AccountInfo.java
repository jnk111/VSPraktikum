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
    public int saldo;

    /**
     * Account uri.
     */
    public String account;

    /**
     * Creates a new instance.
     *
     * @param player
     *            Player uri.
     * @param balance
     *            Account balance.
     */
    public AccountInfo(final String player, final int balance) {
        this(player, balance, null);
    }

    /**
     * Creates a new instance.
     *
     * @param player
     *            Player uri.
     * @param balance
     *            Account balance.
     * @param account
     *            Account uri.
     */
    public AccountInfo(final String player, final int balance, final String account) {
        this.saldo = balance;
        this.player = player;
        this.account = account;
    }

}
