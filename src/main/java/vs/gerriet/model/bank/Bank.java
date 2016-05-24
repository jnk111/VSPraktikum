package vs.gerriet.model.bank;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.exception.AccountAccessException;
import vs.gerriet.exception.TransactionException;
import vs.gerriet.id.BankId;
import vs.gerriet.id.UserId;
import vs.gerriet.id.bank.AccountId;
import vs.gerriet.id.bank.TransactionId;
import vs.gerriet.id.bank.TransferId;
import vs.gerriet.json.AccountInfo;
import vs.gerriet.json.TransferInfo;
import vs.gerriet.model.bank.transaction.AtomicOperation;
import vs.gerriet.model.bank.transaction.AtomicOperation.Type;
import vs.gerriet.model.bank.transaction.Transaction;
import vs.gerriet.model.bank.transaction.Transfer;
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
     * Contains the account url for this bank.
     */
    private String accountsUrl;

    /**
     * Contains the transfer url for this bank.
     */
    private String transferUrl;

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
    public Bank(final BankId id) {
        this.id = id;
        this.accounts = new AccountsContainer();
        this.transactions = new ConcurrentSkipListMap<>();
        this.transfers = new ConcurrentSkipListMap<>();
    }

    /**
     * Adds the given transaction to this bank.
     *
     * @param transaction
     *            Transaction to be added.
     * @return New transaction id for the added transaction.
     */
    public TransactionId addTransaction(final Transaction transaction) {
        final TransactionId transactionId =
                new TransactionId(this.getId(), Integer.valueOf(IdUtils.getUniqueRunntimeId()));
        this.transactions.put(transactionId, transaction);
        return transactionId;
    }

    /**
     * Adds performed transfer.
     *
     * @param transfer
     *            Transfer to be added.
     */
    public void addTransfer(final Transfer transfer) {
        this.transfers.put(transfer.getId(), transfer.getInfo());
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
    public boolean createAccount(final UserId userId) {
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
    public boolean createAccount(final UserId userId, final int balance) {
        return this.accounts.createAccount(userId, new Account(userId, balance));
    }

    /**
     * Deletes the account for the given user.
     *
     * @param user
     *            User id.
     */
    public void deleteAccount(final UserId user) {
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
     * Gets the accounts url.
     *
     * @return Accounts url.
     */
    public String getAccountsUrl() {
        return this.accountsUrl;
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
    public AccountInfo getInfo(final UserId user) throws AccountAccessException {
        return this.accounts.getInfo(user);
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
     * Returns all successful transfer IDs.
     *
     * @return Transfer IDs.
     */
    public String[] getTransfers() {
        final Set<?> keys = this.transfers.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    /**
     * Gets the transfer url.
     *
     * @return Transfer url.
     */
    public String getTransferUrl() {
        return this.transferUrl;
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
     */
    public boolean performTransfer(final AccountId from, final AccountId to, final int amount,
            final String reason) {
        return this.runTransfer(new Transfer(this, from, to, amount, reason));
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
    public boolean performTransfer(final AccountId from, final AccountId to, final int amount,
            final String reason, final TransactionId transactionId) {
        if (!this.transactions.containsKey(transactionId)) {
            return false;
        }
        return this.transactions.get(transactionId)
                .addOperation(new Transfer(this, from, to, amount, reason));
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
     */
    public boolean performTransfer(final AccountId player, final Type type, final int amount,
            final String reason) {
        return this.runTransfer(new Transfer(this, type, player, amount, reason));
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
    public boolean performTransfer(final AccountId player, final Type type, final int amount,
            final String reason, final TransactionId transactionId) {
        if (!this.transactions.containsKey(transactionId)) {
            return false;
        }
        return this.transactions.get(transactionId)
                .addOperation(new Transfer(this, type, player, amount, reason));
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
     * Sets the accounts url.
     *
     * @param url
     *            New accounts url.
     */
    public void setAccountsUrl(final String url) {
        this.accountsUrl = url;
    }

    /**
     * Sets the transfer url.
     *
     * @param url
     *            New transfer url.
     */
    public void setTransferUrl(final String url) {
        this.transferUrl = url;
    }

    /**
     * Runs the given transfer in a simple transaction.
     *
     * @param transfer
     *            Transfer to be run.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     */
    private boolean runTransfer(final Transfer transfer) {
        final Transaction transaction = new Transaction(Transaction.Type.SIMPLE, this);
        transaction.addOperation(transfer);
        try {
            return transaction.commit();
        } catch (final TransactionException ex) {
            // should never happen
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "ERROR"));
        }
        return false;
    }
}