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
import vs.gerriet.model.bank.transaction.AtomicOperation.Type;

/**
 * Controller for transfers from a specific account.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class TransferFromController extends AbstractController implements PostController {

    /**
     * Uri for transfers from a specific account.
     */
    public static final String URI = BankController.URI + "/transfer/from/:from/:amount";

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
        boolean success;
        if (transactionUri == null) {
            try {
                success = bank.performTransfer(player, Type.WITHDRAW, amount, reason);
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
            success = bank.performTransfer(player, Type.WITHDRAW, amount, reason, transactionId);
        }
        // no success: insufficient fonds
        response.status(success ? 201 : 403);
        return "";
    }

}
