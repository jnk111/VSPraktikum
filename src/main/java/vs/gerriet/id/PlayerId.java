package vs.gerriet.id;

import spark.Request;

/**
 * User id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class PlayerId extends Id<String> {

    /**
     * Contains the game id for this player.
     */
    private final GameId game;

    /**
     * Creates a new user id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public PlayerId(final GameId game, final Request request, final String param) {
        super(request, param);
        this.game = game;
    }

    /**
     * Creates a new user id container.
     *
     * @param data
     *            Contained id.
     */
    public PlayerId(final GameId game, final String data) {
        super(data);
        this.game = game;
    }

    @Override
    protected String fromUriSuffix(final String suffix) {
        return suffix;
    }

    @Override
    protected String getUriPrefix() {
        return this.game.getUri();
    }
}
