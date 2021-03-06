package vs.gerriet.service;

import java.net.InetAddress;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.stuff42.error.ExceptionUtils;
import de.stuff42.error.ThreadExceptionHandler;
import spark.Spark;
import vs.gerriet.api.VsApiBase;
import vs.gerriet.api.YellowPages;
import vs.gerriet.controller.ServiceOnlineController;
import vs.gerriet.controller.bank.BankController;
import vs.gerriet.controller.bank.BankListController;
import vs.gerriet.controller.bank.account.AccountController;
import vs.gerriet.controller.bank.account.AccountListController;
import vs.gerriet.controller.bank.transaction.TransactionController;
import vs.gerriet.controller.bank.transaction.TransactionListController;
import vs.gerriet.controller.bank.transfer.TransferController;
import vs.gerriet.controller.bank.transfer.TransferFromController;
import vs.gerriet.controller.bank.transfer.TransferFromToController;
import vs.gerriet.controller.bank.transfer.TransferToController;
import vs.gerriet.controller.bank.transfer.TransferListController;
import vs.gerriet.json.yellowpages.Service;
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

        // do required initialization stuff
        try {
            // setup listen properties
            Spark.ipAddress("0.0.0.0");
            Spark.port(4567);
            // start bank service
            BankService.run();

            // register online controller for yellow pages online state
            ServiceUtils.registerController(
                    new ServiceOnlineController(VsApiBase.GROUP_NAME + " Bank Service"));
            // register within yellow pages
            final String ip = InetAddress.getLocalHost().getHostAddress();
            final Service bankService = new Service(VsApiBase.GROUP_NAME, "Bank service", "banks",
                    "http://" + ip + ":4567/banks");
            final HttpResponse<String> yellowPagesResponse =
                    new YellowPages().registerService(bankService);
            if (yellowPagesResponse == null || yellowPagesResponse.getStatus() != 201) {
                String message = "Failed to register within yellow pages.";
                message += System.lineSeparator();
                if (yellowPagesResponse == null) {
                    message += "Request failed.";
                } else {
                    message += "[" + yellowPagesResponse.getStatus() + "] => "
                            + (new Gson().toJson(yellowPagesResponse.getBody()));
                }
                throw new Exception(message);
            }
        } catch (final Throwable ex) {
            // if we get startup errors, we terminate spark and exit
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "STARTUP"));
            Spark.stop();
            System.exit(-1);
        }
    }

    /**
     * Starts bank service.
     */
    public static void run() {
        // register bank access controllers
        ServiceUtils.registerController(new BankListController());
        ServiceUtils.registerController(new BankController());
        // register transfer controllers
        ServiceUtils.registerController(new TransferListController());
        ServiceUtils.registerController(new TransferController());
        ServiceUtils.registerController(new TransferFromController());
        ServiceUtils.registerController(new TransferToController());
        ServiceUtils.registerController(new TransferFromToController());
        // register transaction controllers
        ServiceUtils.registerController(new TransactionListController());
        ServiceUtils.registerController(new TransactionController());
        // register accounts controllers
        ServiceUtils.registerController(new AccountListController());
        ServiceUtils.registerController(new AccountController());

        // register default error handlers
        Spark.exception(JsonSyntaxException.class, (ex, req, res) -> {
            final String msg = ExceptionUtils.getExceptionInfo(ex, "JSON");
            System.err.println(msg);
            res.status(400);
            res.body(msg);
        });
        Spark.exception(UnirestException.class, (ex, req, res) -> {
            final String msg = ExceptionUtils.getExceptionInfo(ex, "SERVICE");
            System.err.println(msg);
            res.status(504);
            res.body(msg);
        });
    }

    /**
     * Hide the default constructor.
     */
    private BankService() {
        // hide default constructor
    }
}
