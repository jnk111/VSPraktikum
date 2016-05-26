package vs.gerriet.id;

import spark.Request;

/**
 * Service id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class ServiceId extends Id<Integer> {

    /**
     * Creates a new service id container.
     *
     * @param data
     *            Internal service id.
     */
    public ServiceId(final Integer data) {
        super(data);
    }

    /**
     * Creates a new service id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public ServiceId(final Request request, final String param) {
        super(request, param);
    }

    @Override
    protected Integer fromUriSuffix(final String suffix) {
        return Integer.decode(suffix);
    }

    @Override
    protected String getUriPrefix() {
        return "/services/";
    }

}
