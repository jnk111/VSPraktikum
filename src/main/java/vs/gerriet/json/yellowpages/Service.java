package vs.gerriet.json.yellowpages;

/**
 * JSON data object for service data.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Service {
    /**
     * Service group name.
     */
    public String name;
    /**
     * Service description.
     */
    public String description;
    /**
     * Service type.
     */
    public String service;
    /**
     * Service uri.
     */
    public String uri;

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
     */
    public Service(final String name, final String description, final String service,
            final String uri) {
        this.name = name;
        this.description = description;
        this.service = service;
        this.uri = uri;
    }
}
