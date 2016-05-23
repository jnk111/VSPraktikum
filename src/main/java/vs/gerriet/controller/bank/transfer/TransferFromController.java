package vs.gerriet.controller.bank.transfer;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.model.bank.Bank;
import vs.gerriet.model.bank.transaction.AtomicOperation.Type;

public class TransferFromController extends AbstractController implements PostController {

    public static final String URI = BankController.URI + "/transfer/from/:from/:amount/";

    @Override
    public String getUri() {
        return TransferFromController.URI;
    }

    @Override
    public String post(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            response.status(404);
            return "";
        }
        final AccountId player = new AccountId(bank.getId(), request, "from");
        final int amount = Integer.parseInt(request.params("amount"));
        final String reason = this.gson.fromJson(request.body(), String.class);
        final String transactionUri = request.queryParams("transaction");
        if (transactionUri == null) {
            bank.performTransfer(player, Type.WITHDRAW, amount, reason);
        } else {
            final TransactionId transactionId = new TransactionId(bank.getId(), null);
            transactionId.loadUri(transactionUri);
            if (!bank.performTransfer(player, Type.WITHDRAW, amount, reason, transactionId)) {
                // TODO @gerriet-hinrichs: handle error
            }
        }
        // TODO @gerriet-hinrichs: insufficient fonds (403) answer code
        return "";
    }

}
