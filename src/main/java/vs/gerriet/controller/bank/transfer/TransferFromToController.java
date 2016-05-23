package vs.gerriet.controller.bank.transfer;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.bank.BankController;

public class TransferFromToController extends AbstractController implements PostController {
    public static final String URI = BankController.URI + "/transfer/from/:from/to/:to/:amount/";

    @Override
    public String getUri() {
        return TransferFromToController.URI;
    }

    @Override
    public String post(final Request request, final Response response) {
        // TODO @gerriet-hinrichs: implement
        return null;
    }

}
