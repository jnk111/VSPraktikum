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
     * @param game
     *            Game id for the user.
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
     * @param game
     *            Game id for the user.
     * @param data
     *            Contained id.
     */
    public PlayerId(final GameId game, final String data) {
        super(data);
        this.game = game;
    }

    @Override
    public int compareTo(final Id<String> obj) {
        if (obj instanceof PlayerId) {
            final PlayerId other = (PlayerId) obj;
            if (this.game != null) {
                // compare game first
                final int compare = this.game.compareTo(other.game);
                if (compare != 0) {
                    return compare;
                }
            } else {
                if (other.game != null) {
                    // other instance has game set: other is larger
                    return 1;
                }
                // both game field null: compare data
            }
            return super.compareTo(obj);
        }
        // invalid case...
        throw new UnsupportedOperationException("Cannot compare " + this.getClass().getName()
                + " with " + obj.getClass().getName());
    }

    @Override
    protected String fromUriSuffix(final String suffix) {
        return suffix;
    }

    @Override
    protected String getUriPrefix() {
        return this.game.getUri() + "/players/";
    }
}
