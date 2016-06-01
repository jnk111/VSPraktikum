package vs.gerriet.controller.bank.account;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.model.Bank;

/**
 * Controller for account access.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class AccountsController extends AbstractController implements GetController {
    /**
     * Uri for a specific account.
     */
    public static final String URI = AccountsListController.URI + "/:accountid";

    /**
     * Returns info about the selected account.
     */
    @Override
    public String get(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            response.status(404);
            return "";
        }
        final AccountId accountId = new AccountId(bank.getId(), request, "accountid");
        try {
            return this.gson.toJson(bank.getInfo(accountId));
        } catch (@SuppressWarnings("unused") final AccountAccessException ex) {
            // account not found
            response.status(404);
            return "";
        }
    }

    @Override
    public String getUri() {
        return AccountsController.URI;
    }
}
