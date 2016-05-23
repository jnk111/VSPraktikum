package vs.gerriet.service;

import de.stuff42.error.ExceptionUtils;
import de.stuff42.error.ThreadExceptionHandler;
import spark.Spark;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.controller.bank.BanksController;
import vs.gerriet.controller.bank.account.AccountsController;
import vs.gerriet.controller.bank.account.AccountsListController;
import vs.gerriet.controller.bank.transaction.TransactionController;
import vs.gerriet.controller.bank.transaction.TransactionListController;
import vs.gerriet.controller.bank.transfer.TransferController;
import vs.gerriet.controller.bank.transfer.TransferFromController;
import vs.gerriet.controller.bank.transfer.TransferFromToController;
import vs.gerriet.controller.bank.transfer.TransferToController;
import vs.gerriet.controller.bank.transfer.TransfersController;
import vs.gerriet.utils.ServiceUtils;

/**
 * Class providing the bank service.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class BankService {

    /**
     * Registers default error handler and starts bank service.
     *
     * @param argv
     *            Program arguments.
     */
    public static void main(final String[] argv) {
        // set filter for stack trace simplification
        ExceptionUtils.setFilters(new String[] { "vs.gerriet", "de.stuff42" });
        // register global error handling (if something goes horribly wrong)
        ThreadExceptionHandler.registerDefault();
        // setup listen properties
        Spark.ipAddress("0.0.0.0");
        Spark.port(4567);
        // start bank service
        BankService.run();
    }

    /**
     * Starts bank service.
     */
    public static void run() {
        // register bank access controllers
        ServiceUtils.registerController(new BanksController());
        ServiceUtils.registerController(new BankController());
        // register transfer controllers
        ServiceUtils.registerController(new TransfersController());
        ServiceUtils.registerController(new TransferController());
        ServiceUtils.registerController(new TransferFromToController());
        ServiceUtils.registerController(new TransferToController());
        ServiceUtils.registerController(new TransferFromController());
        // register transaction controllers
        ServiceUtils.registerController(new TransactionListController());
        ServiceUtils.registerController(new TransactionController());
        // register accounts controllers
        ServiceUtils.registerController(new AccountsListController());
        ServiceUtils.registerController(new AccountsController());
    }

    /**
     * Hide the default constructor.
     */
    private BankService() {
        // hide default constructor
    }
}
