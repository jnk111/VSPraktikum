package vs.gerriet.api.bank;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.api.VsApiBase;
import vs.gerriet.controller.bank.BanksController;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.json.BankData;
import vs.gerriet.json.BankList;
import vs.gerriet.json.GameIdContainer;

/**
 * Base class for bank data API classes.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
abstract class BankBase extends VsApiBase {

    @Override
    public String getType() {
        return "bank";
    }

    /**
     * API call to create a new bank.
     *
     * @param game
     *            Id of the game the bank should be created for.
     * @return Information about the created bank or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<BankData> requestCreateBank(final GameId game) {
        try {
            return Unirest.post(this.getServiceUri() + BanksController.URI)
                    .body(new GameIdContainer(game.getUri())).asObject(BankData.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to load information for a given bank.
     *
     * @param bank
     *            Id of the bank information should be loaded for.
     * @return Information about the bank or <code>null</code> if the request
     *         failed.
     */
    public HttpResponse<BankData> requestGetBankData(final BankId bank) {
        try {
            return Unirest.get(this.getServiceUri() + bank.getUri()).asObject(BankData.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to load a list with all known banks.
     *
     * @return Bank list or <code>null</code> if the request failed.
     */
    public HttpResponse<BankList> requestGetBankList() {
        try {
            return Unirest.get(this.getServiceUri() + BanksController.URI).asObject(BankList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to update date for a bank.
     *
     * @param bank
     *            Id of the bank that should be updated.
     * @param data
     *            New data.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> requestSetBankData(final BankId bank, final BankData data) {
        try {
            return Unirest.put(this.getServiceUri() + bank.getUri()).body(data).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }
}
