package vs.gerriet.controller.bank.transaction;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.model.bank.Bank;
import vs.gerriet.model.bank.transaction.Transaction.Type;

/**
 * Controller for generic transaction access.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class TransactionListController extends AbstractController
        implements PostController, GetController {
    /**
     * Uri part (used externally).
     */
    public static final String URI_PART = "/transaction";
    /**
     * Generic transaction uri.
     */
    public static final String URI = BankController.URI + TransactionListController.URI_PART;

    /**
     * Returns list with all transactions.
     */
    @Override
    public String get(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            // invalid bank
            response.status(404);
            return "";
        }
        return this.gson.toJson(bank.getTransactions());
    }

    @Override
    public String getUri() {
        return TransactionListController.URI;
    }

    /**
     * Creates a new transaction.
     */
    @Override
    public String post(final Request request, final Response response) {
        final String phases = request.queryParams("phases");
        Type type = Type.SIMPLE;
        switch (phases) {
            case "1-phase":
                type = Type.SIMPLE;
                break;
            case "2-phase":
                type = Type.CHECKED;
                break;
            default:
                response.status(400);
                return "";
        }
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            // invalid bank
            response.status(404);
            return "";
        }
        final TransactionId transactionId = bank.createTransaction(type);
        // send id of the created transaction to client
        return transactionId.getUri();
    }

}
