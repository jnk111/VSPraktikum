package vs.gerriet.api;

import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.controller.bank.BankListController;
import vs.gerriet.controller.bank.account.AccountListController;
import vs.gerriet.controller.bank.transfer.TransferFromController;
import vs.gerriet.controller.bank.transfer.TransferFromToController;
import vs.gerriet.controller.bank.transfer.TransferListController;
import vs.gerriet.controller.bank.transfer.TransferToController;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.GameIdContainer;
import vs.gerriet.json.bank.AccountInfo;
import vs.gerriet.json.bank.AccountList;
import vs.gerriet.json.bank.BankData;
import vs.gerriet.json.bank.BankList;
import vs.gerriet.json.bank.TransactionInfo;
import vs.gerriet.json.bank.TransactionList;
import vs.gerriet.json.bank.TransferInfo;
import vs.gerriet.json.bank.TransferList;

/**
 * Class for bank service API calls.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Bank extends VsApiBase {

    /**
     * <p>
     * API call to commit a transaction.
     * </p>
     * <p>
     * The following status codes are important:
     * <ul>
     * <li><code>200</code> - Transaction successfully committed.</li>
     * <li><code>409</code> - Transaction failed and rolled back.</li>
     * </ul>
     * </p>
     *
     * @param transaction
     *            Transaction to be committed.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> commitTransaction(final TransactionId transaction) {
        try {
            return Unirest.put(this.getServiceUri() + transaction.getUri()).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to confirm a transaction.
     *
     * @param transaction
     *            Transaction to be confirmed.
     * @param account
     *            Account that confirms the transaction.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> confirmTransaction(final TransactionId transaction,
            final AccountId account) {
        try {
            return Unirest.put(this.getServiceUri() + transaction.getUri())
                    .header("content-type", "application/json").body(account.getUri()).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * <p>
     * API call to create an account from the given data.
     * </p>
     * <p>
     * The following status codes are important:
     * <ul>
     * <li><code>201</code> - Account created.</li>
     * <li><code>409</code> - Account found.</li>
     * </ul>
     * </p>
     *
     * @param bank
     *            Bank to create the account on.
     * @param data
     *            Data for the account.
     * @return Response with updated account data or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<AccountInfo> createAccount(final BankId bank, final AccountInfo data) {
        try {
            final String uri = bank.getUri() + AccountListController.URI_PART;
            return Unirest.post(this.getServiceUri() + uri)
                    .header("content-type", "application/json").body(data)
                    .asObject(AccountInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to create a new bank.
     *
     * @param game
     *            Id of the game the bank should be created for.
     * @return Information about the created bank or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<BankData> createBank(final GameId game) {
        try {
            return Unirest.post(this.getServiceUri() + BankListController.URI)
                    .header("content-type", "application/json")
                    .body(new GameIdContainer(game.getUri())).asObject(BankData.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to create a new simple transaction.
     *
     * @param bank
     *            Bank the transaction is created on.
     * @return Response containing the uri of the created transaction within
     *         body or <code>null</code> if the request failed.
     */
    public HttpResponse<String> createTransaction(final BankId bank) {
        return this.createTransaction(bank,
                vs.gerriet.model.bank.transaction.Transaction.Type.SIMPLE);
    }

    /**
     * API call to create a new transaction.
     *
     * @param bank
     *            Bank the transaction is created on.
     * @param type
     *            Transaction type.
     * @return Response containing the uri of the created transaction within
     *         body or <code>null</code> if the request failed.
     */
    public HttpResponse<String> createTransaction(final BankId bank,
            final vs.gerriet.model.bank.transaction.Transaction.Type type) {
        try {
            String phases = "";
            switch (type) {
                case CHECKED:
                    phases = "2-phase";
                    break;
                case SIMPLE:
                    phases = "1-phase";
                    break;
                default:
                    return null;
            }
            return Unirest.post(this.getServiceUri() + bank.getUri() + "/transaction")
                    .queryString("phases", phases).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to get information about a specific account.
     *
     * @param account
     *            Account id.
     * @return Response with account data or <code>null</code> if the request
     *         failed.
     */
    public HttpResponse<AccountInfo> getAccount(final AccountId account) {
        try {
            return Unirest.get(this.getServiceUri() + account.getUri()).asObject(AccountInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to load a list with all accounts for the given bank.
     *
     * @param bank
     *            Bank to load accounts from.
     * @return Response with account list or <code>null</code> if the request
     *         failed.
     */
    public HttpResponse<AccountList> getAccountList(final BankId bank) {
        try {
            final String uri = bank.getUri() + AccountListController.URI_PART;
            return Unirest.get(this.getServiceUri() + uri).asObject(AccountList.class);
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
    public HttpResponse<BankData> getBankData(final BankId bank) {
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
    public HttpResponse<BankList> getBankList() {
        try {
            return Unirest.get(this.getServiceUri() + BankListController.URI)
                    .asObject(BankList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to load a list with all transactions.
     *
     * @param bank
     *            Id of the bank to load the transactions from.
     * @return Response with list of transactions for the given bank or
     *         <code>null</code> if the request failed.
     */
    public HttpResponse<TransactionList> getTransactionList(final BankId bank) {
        try {
            return Unirest.get(this.getServiceUri() + bank.getUri() + "/transaction")
                    .asObject(TransactionList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Returns the status of the given transaction.
     *
     * @param transaction
     *            Transaction id.
     * @return Response containing transaction info or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<TransactionInfo> getTransactionStatus(final TransactionId transaction) {
        try {
            return Unirest.get(this.getServiceUri() + transaction.getUri())
                    .asObject(TransactionInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to get information about the given transfer.
     *
     * @param transfer
     *            Id of the transfer.
     * @return Response with transfer info or <code>null</code> if the request
     *         failed.
     */
    public HttpResponse<TransferInfo> getTransferInfo(final TransferId transfer) {
        try {
            return Unirest.get(this.getServiceUri() + transfer.getUri())
                    .asObject(TransferInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to get all successful transfers from a bank.
     *
     * @param bank
     *            Id of the bank.
     * @return Response with transfer uris or <code>null</code> if the request
     *         failed.
     */
    public HttpResponse<TransferList> getTransferList(final BankId bank) {
        try {
            return Unirest
                    .get(this.getServiceUri() + bank.getUri() + TransferListController.URI_PART)
                    .asObject(TransferList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    @Override
    public String getType() {
        return "bank";
    }

    /**
     * <p>
     * API call to create a transfer from one account to another.
     * </p>
     * <p>
     * Following status codes are important:
     * <ul>
     * <li><code>200</code> - Transfer successful.</li>
     * <li><code>403</code> - Insufficient fonds.</li>
     * </ul>
     * </p>
     *
     * @param from
     *            Account to withdraw money from.
     * @param to
     *            Account to deposit money on.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @return Response with created transfer or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<TransferInfo> performTransfer(final AccountId from, final AccountId to,
            final int amount, final String reason) {
        try {
            if (!from.getBank().equals(to.getBank())) {
                System.err.println("[API] Cannot perform transfer from one bank to another.");
                return null;
            }
            return Unirest
                    .post(this.getServiceUri() + from.getBank().getUri()
                            + TransferFromToController.URI_PART_UNIREST)
                    .routeParam("from", from.getBaseData()).routeParam("to", to.getBaseData())
                    .routeParam("amount", String.valueOf(amount))
                    .header("content-type", "application/json")
                    // cast to object to use object mapper
                    .body((Object) reason).asObject(TransferInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * <p>
     * API call to create a transfer from one account to another within the
     * given transaction.
     * </p>
     * <p>
     * The response will not have status code <code>403</code> for insufficient
     * fonds since this will occur once the transaction is performed.
     * </p>
     *
     * @param from
     *            Account to withdraw money from.
     * @param to
     *            Account to deposit money on.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @param transaction
     *            Transaction to add this transfer to.
     * @return Response with created transfer or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<TransferInfo> performTransfer(final AccountId from, final AccountId to,
            final int amount, final String reason, final TransactionId transaction) {
        try {
            if (!from.getBank().equals(to.getBank())) {
                System.err.println("[API] Cannot perform transfer from one bank to another.");
                return null;
            }
            return Unirest
                    .post(this.getServiceUri() + from.getBank().getUri()
                            + TransferFromToController.URI_PART_UNIREST)
                    .routeParam("from", from.getBaseData()).routeParam("to", to.getBaseData())
                    .routeParam("amount", String.valueOf(amount))
                    .queryString("transaction", transaction.getUri())
                    .header("content-type", "application/json")
                    // cast to object to use object mapper
                    .body((Object) reason).asObject(TransferInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * <p>
     * API call to create a transfer involving one account and the bank.
     * </p>
     * <p>
     * Following status codes are important:
     * <ul>
     * <li><code>200</code> - Transfer successful.</li>
     * <li><code>403</code> - Insufficient fonds.</li>
     * </ul>
     * </p>
     *
     * @param account
     *            Account that is involved in the transfer.
     * @param type
     *            Transfer type.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @return Response with created transfer or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<TransferInfo> performTransfer(final AccountId account,
            final vs.gerriet.model.bank.transaction.AtomicOperation.Type type, final int amount,
            final String reason) {
        try {
            String uri = account.getBank().getUri();
            final Map<String, String> params = new HashMap<>(2);
            switch (type) {
                case DEPOSIT:
                    uri += TransferToController.URI_PART_UNIREST;
                    params.put("to", account.getBaseData());
                    break;
                case WITHDRAW:
                    uri += TransferFromController.URI_PART_UNIREST;
                    params.put("from", account.getBaseData());
                    break;
                default:
                    return null;
            }
            params.put("amount", String.valueOf(amount));
            final HttpRequestWithBody request = Unirest.post(this.getServiceUri() + uri);
            params.forEach((key, value) -> request.routeParam(key, value));
            request.header("content-type", "application/json");
            // cast to object to use object mapper
            request.body((Object) reason);
            return request.asObject(TransferInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * <p>
     * API call to create a transfer involving one account and the bank.
     * </p>
     * <p>
     * The response will not have status code <code>403</code> for insufficient
     * fonds since this will occur once the transaction is performed.
     * </p>
     *
     * @param account
     *            Account that is involved in the transfer.
     * @param type
     *            Transfer type.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @param transaction
     *            Transaction to add this transfer to.
     * @return Response with created transfer or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<TransferInfo> performTransfer(final AccountId account,
            final vs.gerriet.model.bank.transaction.AtomicOperation.Type type, final int amount,
            final String reason, final TransactionId transaction) {
        try {
            String uri = account.getBank().getUri();
            final Map<String, String> params = new HashMap<>(2);
            switch (type) {
                case DEPOSIT:
                    uri += TransferToController.URI_PART_UNIREST;
                    params.put("to", account.getBaseData());
                    break;
                case WITHDRAW:
                    uri += TransferFromController.URI_PART_UNIREST;
                    params.put("from", account.getBaseData());
                    break;
                default:
                    return null;
            }
            params.put("amount", String.valueOf(amount));
            final HttpRequestWithBody request = Unirest.post(this.getServiceUri() + uri);
            params.forEach((key, value) -> request.routeParam(key, value));
            request.queryString("transaction", transaction.getUri());
            request.header("content-type", "application/json");
            // cast to object to use object mapper
            request.body((Object) reason);
            return request.asObject(TransferInfo.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * API call to roll back a transaction.
     *
     * @param transaction
     *            Transaction to be rolled back.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> rollBackTransaction(final TransactionId transaction) {
        try {
            return Unirest.delete(this.getServiceUri() + transaction.getUri()).asString();
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
    public HttpResponse<String> setBankData(final BankId bank, final BankData data) {
        try {
            return Unirest.put(this.getServiceUri() + bank.getUri())
                    .header("content-type", "application/json").body(data).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }
}
