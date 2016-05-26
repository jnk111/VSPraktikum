package vs.gerriet.json.yellowpages;

/**
 * JSON data object for service data with status.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class ServiceStatus extends Service {

    /**
     * Service status.
     */
    public String status;

    /**
     * Creates a new instance form the given data.
     *
     * @param name
     *            Service group name.
     * @param description
     *            Service description.
     * @param service
     *            Service type.
     * @param uri
     *            Service uri.
     * @param status
     *            Service status.
     */
    public ServiceStatus(final String name, final String description, final String service,
            final String uri, final String status) {
        super(name, description, service, uri);
        this.status = status;
    }

}
