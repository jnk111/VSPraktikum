package vs.gerriet.controller.bank.transaction;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.DeleteController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PutController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.PlayerId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.model.Bank;

/**
 * Controller for specific transaction access.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class TransactionController extends AbstractController
        implements GetController, PutController, DeleteController {

    /**
     * Uri to a specific transaction.
     */
    public static final String URI = TransactionListController.URI + "/:transactionId";

    /**
     * Returns the transaction id for the current request.
     *
     * @param bank
     *            Bank instance.
     * @param request
     *            Current request.
     * @return The loaded transaction id or <code>null</code> if the id could
     *         not be loaded.
     */
    private static TransactionId getTransactionId(final Bank bank, final Request request) {
        final String idString = request.params("transactionId");
        if (bank == null || idString == null) {
            return null;
        }
        final TransactionId id = new TransactionId(bank.getId(), null);
        id.loadUriSuffix(idString);
        return id;
    }

    /**
     * Performs a rollback on the transaction.
     */
    @Override
    public String delete(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        final TransactionId id = TransactionController.getTransactionId(bank, request);
        if (id == null) {
            response.status(404);
            return "";
        }
        try {
            bank.rollBackTransaction(id);
        } catch (final TransactionException ex) {
            // something went horribly wrong
            return AbstractController.handleFatalError(ex, response);
        }
        return "";
    }

    /**
     * Sends the status of the transaction.
     */
    @Override
    public String get(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        final TransactionId id = TransactionController.getTransactionId(bank, request);
        if (id == null || !bank.hasTransaction(id)) {
            response.status(404);
            return "";
        }
        return this.gson.toJson(bank.getTransactionInfo(id));
    }

    @Override
    public String getUri() {
        return TransactionController.URI;
    }

    @Override
    public String put(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        final TransactionId id = TransactionController.getTransactionId(bank, request);
        if (id == null || !bank.hasTransaction(id)) {
            response.status(404);
            return "";
        }
        final String userUri = request.body();
        try {
            if (userUri == null || userUri.isEmpty()) {
                if (!bank.commitTransaction(id)) {
                    // indicate that the transaction failed
                    response.status(409);
                }
            } else {
                final PlayerId user = new PlayerId(bank.getGameId(), null);
                user.loadUri(userUri);
                final AccountId account = new AccountId(bank.getId(), user.getBaseData());
                bank.confirmTransaction(id, account);
            }
        } catch (final TransactionException ex) {
            // something went horribly wrong
            return AbstractController.handleFatalError(ex, response);
        }
        return "";
    }
}
