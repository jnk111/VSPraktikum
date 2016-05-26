package vs.gerriet.json.yellowpages;

/**
 * JSON data object for the service list with details.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class ServiceStatusList {
    /**
     * Contains service uris.
     */
    public ServiceStatus[] services;

    /**
     * Creates a new service list.
     *
     * @param services
     *            Service uris.
     */
    public ServiceStatusList(final ServiceStatus[] services) {
        this.services = services;
    }
}
