package vs.gerriet.controller.bank.account;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.id.PlayerId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.json.bank.AccountInfo;
import vs.gerriet.json.bank.AccountList;
import vs.gerriet.model.bank.Bank;

/**
 * Controller for generic account access (account list and account creation).
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class AccountListController extends AbstractController
        implements GetController, PostController {

    /**
     * Uri part after bank uri (extra because used within bank).
     */
    public static final String URI_PART = "/accounts";
    /**
     * Generic account access uri.
     */
    public static final String URI = BankController.URI + AccountListController.URI_PART;

    /**
     * Returns a list of all accounts on the selected bank.
     */
    @Override
    public String get(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            response.status(404);
            return "";
        }
        return this.gson.toJson(new AccountList(bank.getAccounts().getAccounts()));
    }

    @Override
    public String getUri() {
        return AccountListController.URI;
    }

    /**
     * Creates a new user.
     */
    @Override
    public String post(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            response.status(404);
            return "";
        }
        final AccountInfo body = this.gson.fromJson(request.body(), AccountInfo.class);
        final PlayerId user = new PlayerId(bank.getGameId(), null);
        if (!user.loadUri(body.player)) {
            response.status(400);
            return "";
        }
        if (bank.createAccount(user, body.saldo)) {
            response.status(201);
        } else {
            response.status(409);
        }
        try {
            return this.gson.toJson(bank.getInfo(new AccountId(bank.getId(), user.getBaseData())));
        } catch (@SuppressWarnings("unused") final AccountAccessException ex) {
            // we can ignore this exception, we know there is an account for
            // that user
        }
        return "";
    }

}
