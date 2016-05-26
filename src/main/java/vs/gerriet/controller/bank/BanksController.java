package vs.gerriet.controller.bank;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.json.BankData;
import vs.gerriet.json.BankList;
import vs.gerriet.json.GameIdContainer;
import vs.gerriet.model.bank.Bank;
import vs.gerriet.model.bank.BanksContainer;

/**
 * Controller for generic bank access (bank list and bank creation).
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class BanksController extends AbstractController implements GetController, PostController {

    /**
     * Basic uri for banks.
     */
    public static final String URI = "/banks";

    /**
     * Returns a list with all bank URIs.
     */
    @Override
    public String get(final Request request, final Response response) {
        response.type(Controller.MIME_TYPE_JSON);
        return this.gson.toJson(new BankList(BanksContainer.getBanks()));
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
        final GameIdContainer game = this.gson.fromJson(request.body(), GameIdContainer.class);
        if (game == null) {
            response.status(403);
            return "";
        }
        response.type(Controller.MIME_TYPE_JSON);
        final Bank bank = BanksContainer.createBank(game.createGameId());
        return this.gson.toJson(BankData.createFromBank(bank));
    }

}
