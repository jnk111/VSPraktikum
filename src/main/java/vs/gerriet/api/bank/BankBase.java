package vs.gerriet.api.bank;

import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.api.VsApiBase;
import vs.gerriet.controller.bank.BanksController;
import vs.gerriet.controller.bank.account.AccountsListController;
import vs.gerriet.controller.bank.transfer.TransferFromController;
import vs.gerriet.controller.bank.transfer.TransferFromToController;
import vs.gerriet.controller.bank.transfer.TransferToController;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.AccountInfo;
import vs.gerriet.json.AccountList;
import vs.gerriet.json.BankData;
import vs.gerriet.json.BankList;
import vs.gerriet.json.GameIdContainer;
import vs.gerriet.json.TransactionInfo;
import vs.gerriet.json.TransactionList;
import vs.gerriet.json.TransferInfo;
import vs.gerriet.json.TransferList;

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
     * API call to commit a transaction.
     *
     * @param transaction
     *            Transaction to be committed.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> requestCommitTransaction(final TransactionId transaction) {
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
    public HttpResponse<String> requestConfirmTransaction(final TransactionId transaction,
            final AccountId account) {
        try {
            return Unirest.put(this.getServiceUri() + transaction.getUri()).body(account.getUri())
                    .asString();
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
    public HttpResponse<AccountInfo> requestCreateAccount(final BankId bank,
            final AccountInfo data) {
        try {
            final String uri = bank.getUri() + AccountsListController.URI_PART;
            return Unirest.post(this.getServiceUri() + uri).body(data).asObject(AccountInfo.class);
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
     * API call to create a new simple transaction.
     *
     * @param bank
     *            Bank the transaction is created on.
     * @return Response containing the uri of the created transaction within
     *         body or <code>null</code> if the request failed.
     */
    public HttpResponse<String> requestCreateTransaction(final BankId bank) {
        return this.requestCreateTransaction(bank,
                vs.gerriet.model.transaction.Transaction.Type.SIMPLE);
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
    public HttpResponse<String> requestCreateTransaction(final BankId bank,
            final vs.gerriet.model.transaction.Transaction.Type type) {
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
            return Unirest.post(this.getServiceUri() + bank.getUri()).queryString("phases", phases)
                    .asString();
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
    public HttpResponse<AccountInfo> requestGetAccount(final AccountId account) {
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
    public HttpResponse<AccountList> requestGetAccountList(final BankId bank) {
        try {
            final String uri = bank.getUri() + AccountsListController.URI_PART;
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
     * API call to load a list with all transactions.
     *
     * @param bank
     *            Id of the bank to load the transactions from.
     * @return Response with list of transactions for the given bank or
     *         <code>null</code> if the request failed.
     */
    public HttpResponse<TransactionList> requestGetTransactionList(final BankId bank) {
        try {
            return Unirest.get(this.getServiceUri() + bank.getUri())
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
    public HttpResponse<TransactionInfo>
            requestGetTransactionStatus(final TransactionId transaction) {
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
    public HttpResponse<TransferInfo> requestGetTransferInfo(final TransferId transfer) {
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
    public HttpResponse<TransferList> requestGetTransferList(final BankId bank) {
        try {
            return Unirest.get(this.getServiceUri() + bank.getUri()).asObject(TransferList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
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
    public HttpResponse<TransferInfo> requestPerformTransfer(final AccountId from,
            final AccountId to, final int amount, final String reason) {
        try {
            return Unirest.post(this.getServiceUri() + TransferFromToController.URI_PART)
                    .routeParam("from", from.getBaseData()).routeParam("to", to.getBaseData())
                    .routeParam("amount", String.valueOf(amount))
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
    public HttpResponse<TransferInfo> requestPerformTransfer(final AccountId from,
            final AccountId to, final int amount, final String reason,
            final TransactionId transaction) {
        try {
            return Unirest.post(this.getServiceUri() + TransferFromToController.URI_PART)
                    .routeParam("from", from.getBaseData()).routeParam("to", to.getBaseData())
                    .routeParam("amount", String.valueOf(amount))
                    .queryString("transaction", transaction.getUri())
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
    public HttpResponse<TransferInfo> requestPerformTransfer(final AccountId account,
            final vs.gerriet.model.transaction.AtomicOperation.Type type, final int amount,
            final String reason) {
        try {
            String uri = "";
            final Map<String, String> params = new HashMap<>(2);
            switch (type) {
                case DEPOSIT:
                    uri = TransferToController.URI_PART;
                    params.put("to", account.getBaseData());
                    break;
                case WITHDRAW:
                    uri = TransferFromController.URI_PART;
                    params.put("from", account.getBaseData());
                    break;
                default:
                    return null;
            }
            params.put("amount", String.valueOf(amount));
            final HttpRequestWithBody request = Unirest.post(this.getServiceUri() + uri);
            params.forEach((key, value) -> request.routeParam(key, value));
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
    public HttpResponse<TransferInfo> requestPerformTransfer(final AccountId account,
            final vs.gerriet.model.transaction.AtomicOperation.Type type, final int amount,
            final String reason, final TransactionId transaction) {
        try {
            String uri = "";
            final Map<String, String> params = new HashMap<>(2);
            switch (type) {
                case DEPOSIT:
                    uri = TransferToController.URI_PART;
                    params.put("to", account.getBaseData());
                    break;
                case WITHDRAW:
                    uri = TransferFromController.URI_PART;
                    params.put("from", account.getBaseData());
                    break;
                default:
                    return null;
            }
            params.put("amount", String.valueOf(amount));
            final HttpRequestWithBody request = Unirest.post(this.getServiceUri() + uri);
            params.forEach((key, value) -> request.routeParam(key, value));
            request.queryString("transaction", transaction.getUri());
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
    public HttpResponse<String> requestRollBackTransaction(final TransactionId transaction) {
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
    public HttpResponse<String> requestSetBankData(final BankId bank, final BankData data) {
        try {
            return Unirest.put(this.getServiceUri() + bank.getUri()).body(data).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }
}