package vs.gerriet.controller;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.Controller.GetController;

/**
 * Simple controller that always sends a 200 response to indicate that this
 * service is online.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class ServiceOnlineController implements GetController {

    /**
     * Contains the name of this service.
     */
    private final String serviceName;

    /**
     * Creates a new instance for the given service.
     *
     * @param serviceName
     *            Name of the service (used within online response message).
     */
    public ServiceOnlineController(final String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String get(final Request request, final Response response) {
        return this.serviceName + " -> Online";
    }

    @Override
    public String getUri() {
        return "/";
    }
}
