package vs.gerriet.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;

/**
 * Container & Factory for bank instances.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class BanksContainer {

    /**
     * Contains all known bank instances.
     */
    private static Map<BankId, Bank> banks =
            Collections.synchronizedMap(new HashMap<BankId, Bank>());

    /**
     * Creates a bank for the given game. If the game already has a bank,
     * returns that instance.
     *
     * @param game
     *            Game id.
     * @return Found or created Bank instance.
     */
    public static Bank createBank(final GameId game) {
        final BankId id = new BankId(game.getBaseData());
        Bank bank = BanksContainer.getBank(id);
        if (bank == null) {
            bank = new Bank(id, game);
            BanksContainer.banks.put(id, bank);
        }
        return bank;
    }

    /**
     * Removes the bank for the given id.
     *
     * @param id
     *            Bank id.
     */
    public static void deleteBank(final BankId id) {
        BanksContainer.banks.remove(id);
    }

    /**
     * Returns the bank with the given id.
     *
     * @param id
     *            Bank id.
     * @return Bank instance for the given id. Returns <code>null</code> if the
     *         id is invalid.
     */
    public static Bank getBank(final BankId id) {
        if (BanksContainer.banks.containsKey(id)) {
            return BanksContainer.banks.get(id);
        }
        return null;
    }

    /**
     * Returns a list of all known bank instances.
     *
     * @return Bank instance IDs.
     */
    public static String[] getBanks() {
        final String[] res = new String[BanksContainer.banks.size()];
        BankId[] ids = new BankId[res.length];
        ids = BanksContainer.banks.keySet().toArray(ids);
        for (int i = 0; i < res.length; i++) {
            res[i] = ids[i].getUri();
        }
        return res;
    }

    /**
     * Hide the default constructor since this class only contains static
     * methods.
     */
    private BanksContainer() {
        // hide the default constructor
    }
}
