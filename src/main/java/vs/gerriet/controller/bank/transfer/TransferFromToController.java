package vs.gerriet.controller.bank.transfer;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.model.Bank;
import vs.gerriet.model.transaction.Transfer;

/**
 * Controller for transfers involving two accounts.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class TransferFromToController extends AbstractController implements PostController {
    /**
     * Uri part (used externally).
     */
    public static final String URI_PART = "/transfer/from/:from/to/:to/:amount";
    /**
     * Uri for transfers involving two accounts.
     */
    public static final String URI = BankController.URI + TransferFromToController.URI_PART;
    /**
     * Uri part (used externally) with Unirest parameters.
     */
    public static final String URI_PART_UNIREST = "/transfer/from/{from}/to/{to}/{amount}";

    @Override
    public String getUri() {
        return TransferFromToController.URI;
    }

    /**
     * Creates a new transfer involving two accounts.
     */
    @Override
    public String post(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            // invalid bank
            response.status(404);
            return "";
        }
        final AccountId from = new AccountId(bank.getId(), request, "from");
        final AccountId to = new AccountId(bank.getId(), request, "to");
        if (!bank.getAccounts().hasAccount(from) || !bank.getAccounts().hasAccount(to)) {
            // invalid account id
            response.status(404);
            return "";
        }
        final int amount = Integer.parseInt(request.params("amount"));
        final String reason = this.gson.fromJson(request.body(), String.class);
        final String transactionUri = request.queryParams("transaction");
        Transfer transfer = null;
        if (transactionUri == null) {
            try {
                transfer = bank.performTransfer(from, to, amount, reason);
            } catch (final TransactionException ex) {
                // something went horribly wrong
                return AbstractController.handleFatalError(ex, response);
            }
        } else {
            final TransactionId transactionId = new TransactionId(bank.getId(), null);
            transactionId.loadUri(transactionUri);
            if (!bank.hasTransaction(transactionId)) {
                response.status(404);
                return "";
            }
            transfer = bank.performTransfer(from, to, amount, reason, transactionId);
        }
        if (transfer == null || transfer.isFailed()) {
            // no success: insufficient fonds
            response.status(403);
            return "";
        }
        response.status(201);
        return this.gson.toJson(transfer.getInfo());
    }

}
