package vs.aufgabe2b.json.bank;

/**
 * Container object containing a game id.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class GameId {

    /**
     * Game id.
     */
    public String game;

    /**
     * Creates a new instance from the given game id.
     *
     * @param game
     *            Game id to be boxed.
     */
    public GameId(final String game) {
        this.game = game;
    }
}
