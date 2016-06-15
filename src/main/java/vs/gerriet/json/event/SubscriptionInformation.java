package vs.gerriet.json.event;

/**
 * JSON data object for event subscription information data.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class SubscriptionInformation {
    /**
     * Game uri for the event subscriber.
     */
    public String game;
    /**
     * Type of subscribed events.
     */
    public String type;

    /**
     * Subscription notification target.
     */
    public String target;

    /**
     * Creates a new subscription information data object.
     *
     * @param game
     *            Game uri for the subscription.
     * @param type
     *            Event type that should be subscribed.
     * @param target
     *            Target uri for the event notification.
     */
    public SubscriptionInformation(final String game, final String type, final String target) {
        this.game = game;
        this.type = type;
        this.target = target;
    }
}
