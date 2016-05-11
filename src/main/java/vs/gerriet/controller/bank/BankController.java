package vs.gerriet.controller.bank;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PutController;
import vs.gerriet.json.BankData;
import vs.gerriet.model.Bank;
import vs.gerriet.model.BankFactory;

/**
 * Controller for access on a specific bank instance.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class BankController extends AbstractController implements GetController, PutController {

    /**
     * Uri to access a specific bank.
     */
    public static final String URI = BanksController.URI + ":bankid/";

    /**
     * Loads bank instance from query parameter.
     *
     * @param request
     *            Current request to load the parameter from.
     * @return Bank instance or <code>null</code> if the request contains no
     *         valid id.
     */
    public static Bank getBank(final Request request) {
        final String bankId = BanksController.URI + request.params("bankid") + "/";
        return BankFactory.getBank(bankId);
    }

    /**
     * Returns information about the selected bank.
     */
    @Override
    public String get(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        BankData res = null;
        if (bank == null || (res = BankData.createFromBank(bank)) == null) {
            response.status(404);
            return "";
        }
        response.type(Controller.MIME_TYPE_JSON);
        return this.gson.toJson(res);
    }

    @Override
    public String getUri() {
        return BankController.URI;
    }

    /**
     * Updates the selected bank with new urls for accounts and transfers.
     */
    @Override
    public String put(final Request request, final Response response) {
        final String bankId = BanksController.URI + request.params("bankid") + "/";
        final BankData data = this.gson.fromJson(request.body(), BankData.class);
        final Bank bank = BankFactory.getBank(bankId);
        if (bank == null) {
            response.status(404);
            return "";
        }
        bank.setAccountsUrl(data.accounts);
        bank.setTransferUrl(data.transfers);
        return "";
    }
}
