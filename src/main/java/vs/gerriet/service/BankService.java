package vs.gerriet.service;

import de.stuff42.error.ExceptionUtils;
import de.stuff42.error.ThreadExceptionHandler;
import spark.Spark;
import vs.gerriet.controller.Controller;
import vs.gerriet.controller.Controller.DeleteController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.Controller.PutController;
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
        // start bank service
        BankService.run();
    }

    /**
     * Registers the given controller into the spark web-server.
     *
     * @param controller
     *            Controller to be registered.
     */
    private static void registerController(final Controller controller) {
        if (controller instanceof GetController) {
            Spark.get(controller.getUri(), ((GetController) controller)::get);
        }
        if (controller instanceof PostController) {
            Spark.post(controller.getUri(), ((PostController) controller)::post);
        }
        if (controller instanceof PutController) {
            Spark.put(controller.getUri(), ((PutController) controller)::put);
        }
        if (controller instanceof DeleteController) {
            Spark.delete(controller.getUri(), ((DeleteController) controller)::delete);
        }
    }

    /**
     * Registers all controllers for the bank service.
     */
    private static void run() {
        BankService.registerController(new BanksController());
        BankService.registerController(new BankController());
        BankService.registerController(new TransfersController());
        BankService.registerController(new TransferController());
        BankService.registerController(new TransferFromToController());
        BankService.registerController(new TransferToController());
        BankService.registerController(new TransferFromController());
        BankService.registerController(new TransactionListController());
        BankService.registerController(new TransactionController());
        BankService.registerController(new AccountsListController());
        BankService.registerController(new AccountsController());
    }

    /**
     * Hide the default constructor.
     */
    private BankService() {
        // hide default constructor
    }
}
