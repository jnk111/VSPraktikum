package vs.aufgabe2b.models.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import vs.aufgabe2b.models.bank.Bank;
import vs.aufgabe2b.services.BankService;

/**
 * Factory for bank instances.
 *
 * @author Gerriet Hinrichs <gerriet.hinrichs@web.de>
 */
public class BankFactory {

	/**
	 * Contains all known bank instances.
	 */
	private static Map<String, Bank> banks = Collections.synchronizedMap(new HashMap<String, Bank>());

	/**
	 * Creates a bank for the given game. If the game already has a bank, returns
	 * that instance.
	 *
	 * @param game
	 *          Game id.
	 * @return Found or created Bank instance.
	 */
	public static Bank createBank(final String game) {
		// TODO @gerriet-hinrichs: replace "/games/" by constant
		final String regex = "^" + Pattern.quote("/games/");
		final String id = game.replaceAll(regex, BankService.URL_BANKS_BASE);
		Bank bank = BankFactory.getBank(id);
		if (bank == null) {
			bank = new Bank(id);
			BankFactory.banks.put(id, bank);
		}
		return bank;
	}

	/**
	 * Removes the bank for the given id.
	 *
	 * @param id
	 *          Bank id.
	 */
	public static void deleteBank(final String id) {
		BankFactory.banks.remove(id);
	}

	/**
	 * Returns the bank with the given id.
	 *
	 * @param id
	 *          Bank id.
	 * @return Bank instance for the given id. Returns <code>null</code> if the id
	 *         is invalid.
	 */
	public static Bank getBank(final String id) {
		if (BankFactory.banks.containsKey(id)) {
			return BankFactory.banks.get(id);
		}
		return null;
	}

	/**
	 * Returns a list of all known bank instances.
	 *
	 * @return Bank instance IDs.
	 */
	public static String[] getBanks() {
		String[] res = new String[BankFactory.banks.size()];
		res = BankFactory.banks.keySet().toArray(res);
		return res;
	}

	/**
	 * Hide the default constructor since this class only contains static methods.
	 */
	private BankFactory() {
		// hide the default constructor
	}
}
