package vs.gerriet.id;

import spark.Request;

/**
 * User id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class UserId extends Id<String> {

    /**
     * Creates a new user id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public UserId(final Request request, final String param) {
        super(request, param);
    }

    /**
     * Creates a new user id container.
     *
     * @param data
     *            Contained id.
     */
    public UserId(final String data) {
        super(data);
    }

    @Override
    protected String fromUriSuffix(final String suffix) {
        return suffix;
    }

    @Override
    protected String getUriPrefix() {
        return "/users/";
    }
}
