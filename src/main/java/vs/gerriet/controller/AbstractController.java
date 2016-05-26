package vs.gerriet.controller;

import com.google.gson.Gson;

import de.stuff42.error.ExceptionUtils;
import spark.Response;

/**
 * Base class for controllers.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public abstract class AbstractController implements Controller {
    /**
     * Handler for fatal errors. Sets the response code to <code>500</code>,
     * prints the error to the error console and creates an error response
     * string.
     * 
     * @param throwable
     *            Error to be handled.
     * @param response
     *            Response instance for the current request.
     * @return Error message to be sent to the client.
     */
    protected static String handleFatalError(final Throwable throwable, final Response response) {
        response.status(500);
        final String message = ExceptionUtils.getExceptionInfo(throwable, "FATAL");
        System.err.println(message);
        return message;
    }

    /**
     * {@link Gson} instance to read and write json data.
     */
    protected Gson gson = new Gson();
}
