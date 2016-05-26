package vs.gerriet.id;

import spark.Request;

/**
 * Bank id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class GameId extends Id<Integer> {

    /**
     * Creates a new game id container.
     *
     * @param data
     *            Internal game id.
     */
    public GameId(final Integer data) {
        super(data);
    }

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

    @Override
    protected Integer fromUriSuffix(final String suffix) {
        return Integer.decode(suffix);
    }

    @Override
    protected String getUriPrefix() {
        return "/games/";
    }

}
