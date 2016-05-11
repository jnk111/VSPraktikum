package vs.gerriet.controller.bank.transfer;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.json.TransferInfo;
import vs.gerriet.model.Bank;

/**
 * Controller for specific access on a transfer.
 * 
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransferController extends AbstractController implements GetController {

    /**
     * Uri to access a specific transfer on the selected bank.
     */
    public static final String URI = TransfersController.URI + ":transferid/";

    /**
     * Returns information about the selected transfer.
     */
    @Override
    public String get(final Request request, final Response response) {
        final Bank bank = BankController.getBank(request);
        final String transferId = request.params("transferid");
        if (bank == null) {
            response.status(404);
            return "";
        }
        final TransferInfo res = bank.getTransferInfo(transferId);
        if (res == null) {
            // transfer not found
            response.status(404);
            return "";
        }
        return this.gson.toJson(res);
    }

    @Override
    public String getUri() {
        return TransferController.URI;
    }

}
