package vs.gerriet.controller.bank;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.json.BankData;
import vs.gerriet.json.BankList;
import vs.gerriet.json.GameId;
import vs.gerriet.model.Bank;
import vs.gerriet.model.BankFactory;

/**
 * Controller for generic bank access (bank list and bank creation).
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class BanksController extends AbstractController implements GetController, PostController {

    /**
     * Basic uri for banks.
     */
    public static final String URI = "/banks/";

    /**
     * Returns a list with all bank URIs.
     */
    @Override
    public String get(final Request request, final Response response) {
        response.type(Controller.MIME_TYPE_JSON);
        return this.gson.toJson(new BankList(BankFactory.getBanks()));
    }

    @Override
    public String getUri() {
        return BanksController.URI;
    }

    /**
     * Creates a new bank.
     */
    @Override
    public String post(final Request request, final Response response) {
        final GameId game = this.gson.fromJson(request.body(), GameId.class);
        response.type(Controller.MIME_TYPE_JSON);
        final Bank bank = BankFactory.createBank(game.game);
        return this.gson.toJson(BankData.createFromBank(bank));
    }

}
