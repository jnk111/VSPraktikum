package vs.aufgabe2b.services;

import com.google.gson.Gson;

import spark.Spark;
import vs.aufgabe2b.json.bank.BankData;
import vs.aufgabe2b.json.bank.BankList;
import vs.aufgabe2b.json.bank.GameId;
import vs.aufgabe2b.models.bank.Bank;
import vs.aufgabe2b.models.factories.BankFactory;

/**
 * Class providing the bank service.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class BankService {

    /**
     * Base url for the bank service.
     */
    public static final String URL_BANKS_BASE = "/banks/";

    /**
     * Url part for account access.
     */
    public static final String URL_ACCOUNTS_PART = "accounts/";

    /**
     * Url part for transfer access.
     */
    public static final String URL_TRANSFER_PART = "transfer/";

    /**
     * Url part for transfer source specification.
     */
    public static final String URL_TRANSFER_FROM_PART = "from/";

    /**
     * Url part for transfer destination specification.
     */
    public static final String URL_TRANSFER_TO_PART = "to/";

    /**
     * Url part for transaction access.
     */
    public static final String URL_TRANSACTION_PART = "transaction/";

    /**
     * MIME type for JSON data.
     */
    private static final String JSON_MIME_TYPE = "application/json";

    /**
     * Creates (or loads) the bank instance for the given game.
     *
     * @param game
     *            Game id request object.
     * @return Bank data.
     */
    private static BankData createBank(final GameId game) {
        final Bank bank = BankFactory.createBank(game.game);
        return BankData.createFromBank(bank);
    }

    /**
     * Returns bank data for the given bank id.
     *
     * @param id
     *            Bank id.
     * @return Bank data or <code>null</code> if the given id does not exist.
     */
    private static BankData getBank(final String id) {
        final Bank bank = BankFactory.getBank(id);
        if (bank == null) {
            return null;
        }
        return BankData.createFromBank(bank);
    }

    /**
     * Returns the bank list.
     *
     * @return Bank id list.
     */
    private static BankList getBanks() {
        return new BankList(BankFactory.getBanks());
    }

    /**
     * Updates the bank object.
     *
     * @param id
     *            Bank id.
     * @param data
     *            Bank data used for updating.
     * @return <code>true</code> if the bank exists, <code>false</code>
     *         otherwise.
     */
    private static boolean updateBank(final String id, final BankData data) {
        final Bank bank = BankFactory.getBank(id);
        if (bank == null) {
            return false;
        }
        bank.setAccountsUrl(data.accounts);
        bank.setTransferUrl(data.transfers);
        return true;
    }

    /**
     * {@link Gson} instance used to encode responses.
     */
    private final Gson gson = new Gson();

    /**
     * Registers all urls for the bank service.
     */
    public BankService() {
        // bank list
        Spark.get(BankService.URL_BANKS_BASE, (reqest, response) -> {
            response.type(BankService.JSON_MIME_TYPE);
            return BankService.getBanks();
        }, this.gson::toJson);

        // bank creation
        Spark.post(BankService.URL_BANKS_BASE, (request, response) -> {
            final GameId game = this.gson.fromJson(request.body(), GameId.class);
            response.type(BankService.JSON_MIME_TYPE);
            return BankService.createBank(game);
        }, this.gson::toJson);

        // bank getter
        Spark.get(BankService.URL_BANKS_BASE + ":id/", (request, response) -> {
            final String bankId = BankService.URL_BANKS_BASE + request.params("id") + "/";
            response.type(BankService.JSON_MIME_TYPE);
            final BankData res = BankService.getBank(bankId);
            if (res == null) {
                response.status(404);
                return null;
            }
            return res;
        }, this.gson::toJson);

        // bank data setter
        Spark.put(BankService.URL_BANKS_BASE + ":id/", (request, response) -> {
            final String bankId = BankService.URL_BANKS_BASE + request.params("id") + "/";
            final BankData data = this.gson.fromJson(request.body(), BankData.class);
            if (BankService.updateBank(bankId, data)) {
                response.status(200);
            } else {
                response.status(404);
            }
            return "";
        });

        // TODO @gerriet-hinrichs: add missing urls
    }
}
