package vs.gerriet.model;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import vs.gerriet.controller.bank.account.AccountsListController;
import vs.gerriet.controller.bank.transfer.TransfersController;
import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.BankId;
import vs.gerriet.id.GameId;
import vs.gerriet.id.PlayerId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.AccountInfo;
import vs.gerriet.json.TransactionInfo;
import vs.gerriet.json.TransactionList;
import vs.gerriet.json.TransferInfo;
import vs.gerriet.json.TransferList;
import vs.gerriet.model.transaction.AtomicOperation;
import vs.gerriet.model.transaction.AtomicOperation.Type;
import vs.gerriet.model.transaction.Transaction;
import vs.gerriet.model.transaction.Transfer;
import vs.gerriet.utils.IdUtils;

/**
 * <p>
 * Thread safe bank with transaction support.
 * </p>
 * <p>
 * Account interaction is done by using locks on per account basis to allow
 * multiple interactions that don't influence each other at the same time.
 * </p>
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class Bank {
    /**
     * Contains the bank's id.
     */
    private final BankId id;
    /**
     * Contains the id of the game this bank is associated to.
     */
    private final GameId gameId;
    /**
     * Contains the account uri for this bank.
     */
    private final String accountsUri;
    /**
     * Contains the transfer uri for this bank.
     */
    private final String transferUri;

    /**
     * Contains the bank accounts.
     */
    private final AccountsContainer accounts;

    /**
     * Contains performed transfers.
     */
    private final Map<TransferId, TransferInfo> transfers;

    /**
     * Contains active transactions.
     */
    private final Map<TransactionId, Transaction> transactions;

    /**
     * Creates a new bank without any accounts.
     */
    public Bank(final BankId id, final GameId gameId) {
        this.id = id;
        this.gameId = gameId;
        this.accounts = new AccountsContainer();
        this.transactions = new ConcurrentSkipListMap<>();
        this.transfers = new ConcurrentSkipListMap<>();
        this.accountsUri = this.id.getUri() + AccountsListController.URI_PART;
        this.transferUri = this.id.getUri() + TransfersController.URI_PART;
    }

    /**
     * Adds transfer.
     *
     * @param transfer
     *            Transfer to be added.
     */
    public void addTransfer(final Transfer transfer) {
        this.transfers.put(transfer.getId(), transfer.getInfo());
    }

    /**
     * Commits the given transaction.
     *
     * @param transactionId
     *            Transaction id.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     * @throws TransactionException
     *             If something went horribly wrong.
     */
    public boolean commitTransaction(final TransactionId transactionId)
            throws TransactionException {
        if (!this.hasTransaction(transactionId)) {
            return false;
        }
        return this.transactions.get(transactionId).commit();
    }

    /**
     * Confirms the given transaction for the given account.
     *
     * @param transactionId
     *            Transaction id.
     * @param account
     *            Account id.
     */
    public void confirmTransaction(final TransactionId transactionId, final AccountId account) {
        if (!this.hasTransaction(transactionId)) {
            return;
        }
        this.transactions.get(transactionId).confirm(account);
    }

    /**
     * Creates a new account for the given user with balance <code>0</code>.
     *
     * @param userId
     *            User id.
     * @return <code>true</code> on success, <code>false</code> if an account
     *         for the given user already exists.
     * @see #createAccount(String, int)
     */
    public boolean createAccount(final PlayerId userId) {
        return this.createAccount(userId, 0);
    }

    /**
     * Creates a new account for the given user with the given balance.
     *
     * @param userId
     *            User id.
     * @param balance
     *            Account balance.
     * @return <code>true</code> on success, <code>false</code> if an account
     *         for the given user already exists.
     */
    public boolean createAccount(final PlayerId userId, final int balance) {
        return this.accounts.createAccount(userId, new Account(userId, balance));
    }

    /**
     * Adds a new transaction to this bank.
     *
     * @param type
     *            Transaction type.
     * @return New transaction id for the added transaction.
     */
    public TransactionId
            createTransaction(final vs.gerriet.model.transaction.Transaction.Type type) {
        final TransactionId transactionId =
                new TransactionId(this.getId(), Integer.valueOf(IdUtils.getUniqueRunntimeId()));
        final Transaction transaction = new Transaction(transactionId, type, this);
        this.transactions.put(transactionId, transaction);
        return transactionId;
    }

    /**
     * Deletes the account for the given user.
     *
     * @param user
     *            User id.
     */
    public void deleteAccount(final PlayerId user) {
        this.accounts.deleteAccount(user);
    }

    /**
     * Returns all accounts within this bank instance.
     *
     * @return Accounts for this bank.
     */
    public AccountsContainer getAccounts() {
        return this.accounts;
    }

    /**
     * Gets the accounts uri.
     *
     * @return Accounts uri.
     */
    public String getAccountsUri() {
        return this.accountsUri;
    }

    /**
     * Returns the id of the game this bank is associated to.
     *
     * @return Game id.
     */
    public GameId getGameId() {
        return this.gameId;
    }

    /**
     * Returns the bank's id.
     *
     * @return Bank id.
     */
    public BankId getId() {
        return this.id;
    }

    /**
     * Returns information about the given user account.
     *
     * @param account
     *            Account id.
     * @return Current balance.
     * @throws AccountAccessException
     *             If the user account could not be locked or if it does not
     *             exist.
     */
    public AccountInfo getInfo(final AccountId account) throws AccountAccessException {
        return this.accounts.getInfo(account);
    }

    /**
     * Returns information about the given user account.
     *
     * @param user
     *            User id.
     * @return Current balance.
     * @throws AccountAccessException
     *             If the user account could not be locked or if it does not
     *             exist.
     */
    public AccountInfo getInfo(final PlayerId user) throws AccountAccessException {
        return this.accounts.getInfo(user);
    }

    /**
     * Returns transaction info for the given transaction.
     *
     * @param transactionId
     *            Id of the transaction
     * @return Transaction info.
     */
    public TransactionInfo getTransactionInfo(final TransactionId transactionId) {
        if (this.hasTransaction(transactionId)) {
            return this.transactions.get(transactionId).getInfo();
        }
        return null;
    }

    /**
     * Returns list with all transactions.
     *
     * @return Transaction list.
     */
    public TransactionList getTransactions() {
        final Set<TransactionId> keys = this.transactions.keySet();
        return new TransactionList(keys.toArray(new String[keys.size()]));
    }

    /**
     * Returns the transfer info for the transfer with the given id.
     *
     * @param transferId
     *            Id of the transfer to load info from.
     * @return Transfer information. Returns <code>null</code> if no transfer
     *         was found for the given instance.
     */
    public TransferInfo getTransferInfo(final String transferId) {
        return this.transfers.get(transferId);
    }

    /**
     * Returns all transfer uris.
     *
     * @return Transfer list.
     */
    public TransferList getTransfers() {
        final Set<TransferId> keys = this.transfers.keySet();
        return new TransferList(keys.toArray(new String[keys.size()]));
    }

    /**
     * Gets the transfer uri.
     *
     * @return Transfer uri.
     */
    public String getTransferUri() {
        return this.transferUri;
    }

    /**
     * Checks if the given transaction id is valid.
     *
     * @param transactionId
     *            Transaction id.
     * @return <code>true</code> if the transaction is active,
     *         <code>false</code> otherwise.
     */
    public boolean hasTransaction(final TransactionId transactionId) {
        return this.transactions.containsKey(transactionId);
    }

    /**
     * Performs the given atomic operation.
     *
     * @param operation
     *            Action to be performed.
     * @throws AccountAccessException
     *             if the operation could not be performed. There where no
     *             changes if this exception is thrown.
     */
    public void performAtomicOperation(final AtomicOperation operation)
            throws AccountAccessException {
        if (!operation.getBank().equals(this)) {
            throw new AccountAccessException("The operation belong to another bank instance.");
        }
        final Account account = this.accounts.getAccount(operation.getAccount());
        switch (operation.getType()) {
            case DEPOSIT:
                account.deposit(operation.getAmount());
                break;
            case WITHDRAW:
                account.withdraw(operation.getAmount());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Performs a transfer on this bank.
     *
     * @param from
     *            Account to transfer from.
     * @param to
     *            Account to transfer to.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     *
     * @return <code>true</code> on success, <code>false</code> otherwise.
     * @throws TransactionException
     *             If something went horribly wrong.
     */
    public Transfer performTransfer(final AccountId from, final AccountId to, final int amount,
            final String reason) throws TransactionException {
        final Transfer transfer = new Transfer(this, from, to, amount, reason);
        this.runTransfer(transfer);
        return transfer;
    }

    /**
     * Performs a transfer on this bank.
     *
     * @param from
     *            Account to transfer from.
     * @param to
     *            Account to transfer to.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @param transactionId
     *            Transaction for this transfer.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     */
    public Transfer performTransfer(final AccountId from, final AccountId to, final int amount,
            final String reason, final TransactionId transactionId) {
        if (!this.transactions.containsKey(transactionId)) {
            return null;
        }
        final Transfer transfer = new Transfer(this, from, to, amount, reason);
        this.transactions.get(transactionId).addOperation(transfer);
        return transfer;
    }

    /**
     * Performs a bank transfer on this bank.
     *
     * @param player
     *            Account to perform the transfer on.
     * @param type
     *            Transfer type.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     * @throws TransactionException
     *             If something went horribly wrong.
     */
    public Transfer performTransfer(final AccountId player, final Type type, final int amount,
            final String reason) throws TransactionException {
        final Transfer transfer = new Transfer(this, type, player, amount, reason);
        this.runTransfer(transfer);
        return transfer;
    }

    /**
     * Performs a bank transfer on this bank.
     *
     * @param player
     *            Account to perform the transfer on.
     * @param type
     *            Transfer type.
     * @param amount
     *            Transfer amount.
     * @param reason
     *            Transfer reason.
     * @param transactionId
     *            Transaction for this transfer.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     */
    public Transfer performTransfer(final AccountId player, final Type type, final int amount,
            final String reason, final TransactionId transactionId) {
        if (!this.transactions.containsKey(transactionId)) {
            return null;
        }
        final Transfer transfer = new Transfer(this, type, player, amount, reason);
        this.transactions.get(transactionId).addOperation(transfer);
        return transfer;
    }

    /**
     * Removes the transaction with the given id from this bank.
     *
     * @param transaction
     *            Id of the transaction to be removed.
     */
    public void removeTransaction(final TransactionId transaction) {
        this.transactions.remove(transaction);
    }

    /**
     * Rolls back the transaction with the given id.
     *
     * @param transactionId
     *            Transaction id.
     * @throws TransactionException
     *             If something went horribly wrong.
     */
    public void rollBackTransaction(final TransactionId transactionId) throws TransactionException {
        if (!this.hasTransaction(transactionId)) {
            return;
        }
        this.transactions.get(transactionId).rollback();
    }

    /**
     * Runs the given transfer in a simple transaction.
     *
     * @param transfer
     *            Transfer to be run.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     * @throws TransactionException
     *             If something went horribly wrong.
     */
    private boolean runTransfer(final Transfer transfer) throws TransactionException {
        final TransactionId transactionId = this.createTransaction(Transaction.Type.SIMPLE);
        final Transaction transaction = this.transactions.get(transactionId);
        transaction.addOperation(transfer);
        return transaction.commit();
    }
}
