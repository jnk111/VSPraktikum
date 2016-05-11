package vs.gerriet.controller.bank.transaction;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.DeleteController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PutController;

public class TransactionController extends AbstractController
        implements GetController, PutController, DeleteController {

    @Override
    public String delete(final Request request, final Response response) {
        // TODO @gerriet-hinrichs: implement
        return null;
    }

    @Override
    public String get(final Request request, final Response response) {
        // TODO @gerriet-hinrichs: implement
        return null;
    }

    @Override
    public String getUri() {
        // TODO @gerriet-hinrichs: implement
        return null;
    }

    @Override
    public String put(final Request request, final Response response) {
        // TODO @gerriet-hinrichs: implement
        return null;
    }
}
