package vs.gerriet.id;

import spark.Request;

/**
 * Bank id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class GameId extends Id<String> {

    /**
     * Creates a new game id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public GameId(final Request request, final String param) {
        super(request, param);
    }

    /**
     * Creates a new game id container.
     *
     * @param data
     *            Internal game id.
     */
    public GameId(final String data) {
        super(data);
    }

    @Override
    protected String fromUriSuffix(final String suffix) {
        return suffix;
    }

    @Override
    protected String getUriPrefix() {
        return "/games/";
    }

}
