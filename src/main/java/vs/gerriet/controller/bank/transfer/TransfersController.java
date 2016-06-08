package vs.gerriet.controller.bank.transfer;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.json.TransferList;
import vs.gerriet.model.Bank;

/**
 * Controller to access the transfer list.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransfersController extends AbstractController implements GetController {

    /**
     * Uri part after bank uri (extra because used within bank).
     */
    public static final String URI_PART = "/transfers";
    /**
     * Uri for the transfer list.
     */
    public static final String URI = BankController.URI + TransfersController.URI_PART;

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
        final TransferList transfers = bank.getTransfers();
        return this.gson.toJson(transfers);
    }

    @Override
    public String getUri() {
        return TransfersController.URI;
    }

}
