package vs.gerriet.json;

import vs.gerriet.id.GameId;

/**
 * Container object containing a game id.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class GameIdContainer {

    /**
     * Game id.
     */
    public String game;

    /**
     * Creates a new instance from the given game uri.
     *
     * @param game
     *            Game id to be boxed.
     */
    public GameIdContainer(final String game) {
        this.game = game;
    }

    /**
     * Creates a game id from this container.
     * 
     * @return Created game id or <code>null</code> if the contained id is
     *         invalid.
     */
    public GameId createGameId() {
        final GameId gameId = new GameId(null);
        if (gameId.loadUri(this.game)) {
            return gameId;
        }
        return null;
    }
}
