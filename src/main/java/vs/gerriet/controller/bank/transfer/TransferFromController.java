package vs.gerriet.controller.bank.transfer;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.model.bank.Bank;
import vs.gerriet.model.bank.transaction.Transfer;
import vs.gerriet.model.bank.transaction.AtomicOperation.Type;

/**
 * Controller for transfers from a specific account.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class TransferFromController extends AbstractController implements PostController {
    /**
     * Uri part (used externally).
     */
    public static final String URI_PART = "/transfer/from/:from/:amount";
    /**
     * Uri for transfers from a specific account.
     */
    public static final String URI = BankController.URI + TransferFromController.URI_PART;
    /**
     * Uri part (used externally) with Unirest parameters.
     */
    public static final String URI_PART_UNIREST = "/transfer/from/{from}/{amount}";

    @Override
    public String getUri() {
        return TransferFromController.URI;
    }

    /**
     * Creates a new transfer from a specific account.
     */
    @Override
    public String post(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            // invalid bank
            response.status(404);
            return "";
        }
        final AccountId player = new AccountId(bank.getId(), request, "from");
        if (!bank.getAccounts().hasAccount(player)) {
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
                transfer = bank.performTransfer(player, Type.WITHDRAW, amount, reason);
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
            transfer = bank.performTransfer(player, Type.WITHDRAW, amount, reason, transactionId);
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
