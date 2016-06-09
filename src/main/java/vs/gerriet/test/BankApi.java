package vs.gerriet.test;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;

import spark.Spark;
import vs.gerriet.api.Bank;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.id.PlayerId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.AccountInfo;
import vs.gerriet.json.TransactionList;
import vs.gerriet.json.TransferList;
import vs.gerriet.model.transaction.AtomicOperation.Type;
import vs.gerriet.service.BankService;

/**
 * Demo class for the bank service and the service API.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class BankApi {

    /**
     * Runs a small test application that uses the bank service.
     *
     * @param args
     *            Program arguments.
     */
    public static void main(final String[] args) {
        // start bank service locally
        System.out.println("Starting bank service on localhost ...");
        BankService.run();

        // use: vs.gerriet.api.Bank
        final Bank bankApi = new Bank();

        // set service uri to localhost (for debug), remove to use yellow pages.
        Bank.setServiceUri("http://localhost:4567");

        // list banks
        System.out.println("Listing banks ...");
        BankApi.printResponse(bankApi.getBankList());

        // create bank
        System.out.println("Creating bank ...");
        final GameId gameId = new GameId("test");
        BankApi.printResponse(bankApi.createBank(gameId));

        // get bank info
        System.out.println("Loading bank info ...");
        final BankId bankId = new BankId(gameId.getBaseData());
        BankApi.printResponse(bankApi.getBankData(bankId));

        // get account list
        System.out.println("Listing bank accounts ...");
        BankApi.printResponse(bankApi.getAccountList(bankId));

        // create accounts
        System.out.println("Creating player account #1 ...");
        final PlayerId playerId1 = new PlayerId(gameId, "foo");
        BankApi.printResponse(
                bankApi.createAccount(bankId, new AccountInfo(playerId1.getUri(), 42)));
        System.out.println("Creating player account #2 ...");
        final PlayerId playerId2 = new PlayerId(gameId, "bar");
        BankApi.printResponse(
                bankApi.createAccount(bankId, new AccountInfo(playerId2.getUri(), 42)));

        // get account list
        System.out.println("Listing bank accounts ...");
        BankApi.printResponse(bankApi.getAccountList(bankId));

        // listing transfers
        System.out.println("Listing transfers ...");
        BankApi.printResponse(bankApi.getTransferList(bankId));

        // list account info
        final AccountId accountId1 = new AccountId(bankId, playerId1.getBaseData());
        final AccountId accountId2 = new AccountId(bankId, playerId2.getBaseData());

        System.out.println("Getting info for account #1 ...");
        BankApi.printResponse(bankApi.getAccount(accountId1));

        System.out.println("Getting info for account #2 ...");
        BankApi.printResponse(bankApi.getAccount(accountId2));

        // print transfers
        BankApi.printTransfers(bankApi, bankId);

        // perform transfers
        System.out.println("Performing transfer #1 ...");
        BankApi.printResponse(bankApi.performTransfer(accountId1, Type.DEPOSIT, 1337, "baz #1"));

        System.out.println("Performing transfer #2 ...");
        BankApi.printResponse(bankApi.performTransfer(accountId1, accountId2, 1337, "baz #2"));

        System.out.println("Performing transfer #3 ...");
        BankApi.printResponse(bankApi.performTransfer(accountId2, Type.WITHDRAW, 1337, "baz #3"));

        // print transfers
        BankApi.printTransfers(bankApi, bankId);

        // print transactions
        BankApi.printTransactions(bankApi, bankId);

        // start transaction
        System.out.println("Starting simple transaction #1 ...");
        final HttpResponse<String> transactionUriResponse1 = bankApi.createTransaction(bankId);
        BankApi.printResponse(transactionUriResponse1);
        final TransactionId transactionId1 = new TransactionId(bankId, null);
        transactionId1.loadUri(transactionUriResponse1.getBody());

        // perform transfers within transaction
        System.out.println("Performing transaction #1 transfer #1 ...");
        BankApi.printResponse(
                bankApi.performTransfer(accountId1, Type.DEPOSIT, 1337, "baz #1", transactionId1));

        System.out.println("Performing transaction #1 transfer #2 ...");
        BankApi.printResponse(
                bankApi.performTransfer(accountId1, accountId2, 1337, "baz #2", transactionId1));

        System.out.println("Performing transaction #1 transfer #3 ...");
        BankApi.printResponse(
                bankApi.performTransfer(accountId2, Type.WITHDRAW, 1337, "baz #3", transactionId1));

        // print transactions
        BankApi.printTransactions(bankApi, bankId);

        // roll back transaction
        System.out.println("Rolling back transaction #1 ...");
        BankApi.printResponse(bankApi.rollBackTransaction(transactionId1));

        // print transactions
        BankApi.printTransactions(bankApi, bankId);

        // print transfers
        BankApi.printTransfers(bankApi, bankId);

        // start transaction
        System.out.println("Starting simple transaction #1 ...");
        final HttpResponse<String> transactionUriResponse2 = bankApi.createTransaction(bankId);
        BankApi.printResponse(transactionUriResponse2);
        final TransactionId transactionId2 = new TransactionId(bankId, null);
        transactionId2.loadUri(transactionUriResponse2.getBody());

        // print transactions
        BankApi.printTransactions(bankApi, bankId);

        // perform transfers within transaction
        System.out.println("Performing transaction #2 transfer #1 ...");
        BankApi.printResponse(
                bankApi.performTransfer(accountId1, Type.DEPOSIT, 1337, "baz #1", transactionId2));

        System.out.println("Performing transaction #2 transfer #2 ...");
        BankApi.printResponse(
                bankApi.performTransfer(accountId1, accountId2, 1337, "baz #2", transactionId2));

        System.out.println("Performing transaction #2 transfer #3 ...");
        BankApi.printResponse(
                bankApi.performTransfer(accountId2, Type.WITHDRAW, 1337, "baz #3", transactionId2));

        // commit transaction
        System.out.println("Committing transaction #2 ...");
        BankApi.printResponse(bankApi.commitTransaction(transactionId2));

        // print transactions
        BankApi.printTransactions(bankApi, bankId);

        // print transfers
        BankApi.printTransfers(bankApi, bankId);

        // stop bank service
        System.out.println("Stopping bank service ...");
        Spark.stop();
    }

    /**
     * Prints the response using Gson.
     *
     * @param res
     *            Response to be printed.
     */
    private static void printResponse(final HttpResponse<? extends Object> res) {
        if (res == null) {
            System.out.println("NULL");
            return;
        }
        // gson used here for object printing
        final Gson gson = new Gson();
        System.out.println("[" + res.getStatus() + "] => " + gson.toJson(res.getBody()));

    }

    /**
     * Prints a list with all transaction also containing detailed information.
     *
     * @param bankApi
     *            Bank API instance to be used.
     * @param bankId
     *            Id of the bank the transactions should be printed for.
     */
    private static void printTransactions(final Bank bankApi, final BankId bankId) {
        // list transactions
        System.out.println("Listing transactions ...");
        final HttpResponse<TransactionList> transactionListResponse =
                bankApi.getTransactionList(bankId);
        BankApi.printResponse(transactionListResponse);

        // get transaction info
        if (transactionListResponse != null) {
            System.out.println("Getting transaction status ...");
            final TransactionId transactionId = new TransactionId(bankId, null);
            for (final String transactionUri : transactionListResponse.getBody().transactions) {
                transactionId.loadUri(transactionUri);
                BankApi.printResponse(bankApi.getTransactionStatus(transactionId));
            }
        }
    }

    /**
     * Prints a list with all transfers also containing detailed information.
     *
     * @param bankApi
     *            Bank API instance to be used.
     * @param bankId
     *            Id of the bank the transfers should be printed for.
     */
    private static void printTransfers(final Bank bankApi, final BankId bankId) {
        // listing transfers
        System.out.println("Listing transfers ...");
        final HttpResponse<TransferList> transferListResponse = bankApi.getTransferList(bankId);
        BankApi.printResponse(transferListResponse);

        // getting transfer info
        if (transferListResponse != null) {
            System.out.println("Getting transfer info ...");
            final TransferId transferId = new TransferId(bankId, null);
            for (final String transferUri : transferListResponse.getBody().transfers) {
                transferId.loadUri(transferUri);
                BankApi.printResponse(bankApi.getTransferInfo(transferId));
            }
        }
    }
}
