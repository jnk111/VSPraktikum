package vs.gerriet.test;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.api.Bank;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.json.AccountInfo;

public class BankApi {

    public static void main(final String[] args) {

        // use: vs.gerriet.api.Bank
        final Bank bankApi = new Bank();

        // set service uri to localhost (for debug), remove to use yellow pages.
        Bank.setServiceUri("http://localhost:4567");

        // list banks
        BankApi.printResponse(bankApi.getBankList());

        // create bank
        final GameId gameId = new GameId("test");
        BankApi.printResponse(bankApi.createBank(gameId));

        // get bank info
        final BankId bankId = new BankId(gameId.getBaseData());
        BankApi.printResponse(bankApi.getBankData(bankId));

        // get account list
        BankApi.printResponse(bankApi.getAccountList(bankId));

        // create account
        BankApi.printResponse(bankApi.createAccount(bankId, new AccountInfo("/players/bla", 43)));

    }

    private static void printResponse(final HttpResponse<? extends Object> res) {
        if (res == null) {
            System.out.println("NULL");
            return;
        }
        // gson used here for object printing
        final Gson gson = new Gson();
        System.out.println("[" + res.getStatus() + "] => " + gson.toJson(res.getBody()));

    }
}
