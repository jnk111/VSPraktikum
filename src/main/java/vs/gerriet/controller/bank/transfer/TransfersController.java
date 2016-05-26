package vs.gerriet.controller.bank.transfer;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.model.bank.Bank;

/**
 * Controller to access the transfer list.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransfersController extends AbstractController implements GetController {

    /**
     * Uri for the transfer list.
     */
    public static final String URI = BankController.URI + "/transfers";

    /**
     * Returns a list with all transfer IDs.
     */
    @Override
    public String get(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        if (bank == null) {
            response.status(404);
            return "";
        }
        return this.gson.toJson(bank.getTransfers());
    }

    @Override
    public String getUri() {
        return TransfersController.URI;
    }

}
