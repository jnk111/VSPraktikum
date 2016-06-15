package vs.gerriet.json.event;

/**
 * JSON data object used to add/remove event subscriptions.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class SubscriptionRegisterData {
    /**
     * Uri of the game notifications should be subscribed to.
     */
    public String game;
    /**
     * Uri where notifications should be posted to.
     */
    public String uri;
    /**
     * List of events subscription should be changed for.
     */
    public String[] events;

    /**
     * Creates a new JSON data object.
     * 
     * @param game
     *            Game uri.
     * @param uri
     *            Notification target uri.
     * @param events
     *            Event names for subscription.
     */
    public SubscriptionRegisterData(final String game, final String uri, final String[] events) {
        this.game = game;
        this.uri = uri;
        this.events = events;
    }
}
