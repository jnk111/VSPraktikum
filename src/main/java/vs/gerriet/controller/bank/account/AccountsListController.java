package vs.gerriet.controller.bank.account;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.controller.bank.BanksController;
import vs.gerriet.id.UserId;
import vs.gerriet.json.AccountInfo;
import vs.gerriet.model.bank.Bank;

/**
 * Controller for generic account access (account list and account creation).
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class AccountsListController extends AbstractController
        implements GetController, PostController {

    /**
     * Generic account access uri.
     */
    public static final String URI = BanksController.URI + "accounts/";

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
        return this.gson.toJson(bank.getAccounts());
    }

    @Override
    public String getUri() {
        return AccountsListController.URI;
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
        final UserId user = new UserId(null);
        user.loadUri(body.player);
        if (bank.createAccount(user, body.balance)) {
            response.status(201);
        } else {
            response.status(409);
        }
        return "";
    }

}
