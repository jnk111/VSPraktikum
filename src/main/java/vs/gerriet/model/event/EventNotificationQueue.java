package vs.gerriet.model.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.json.event.SubscriptionInformation;
import vs.gerriet.json.event.SubscriptionRegisterData;
import vs.jonas.services.json.EventData;

/**
 * Queue to asynchronous notify subscribers about given events.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class EventNotificationQueue extends AsyncConsumerQueue<EventData> {

    /**
     * <p>
     * Contains event subscriber information.
     * </p>
     * <p>
     * The first level key is the game uri.
     * </p>
     * <p>
     * The second level key is the event type.
     * </p>
     * <p>
     * The set at third level contains all uris that should be notified for the
     * event matching the given path.
     * </p>
     */
    private final Map<String, Map<String, Set<String>>> subscribers = new ConcurrentSkipListMap<>();

    /**
     * Creates a new queue. The internal consumer will notify subscribers.
     */
    public EventNotificationQueue() {
        super();
        // we use the parent constructor without arguments because we want to
        // use member fields within the consumer.
        this.consumer = event -> {
            // do we have subscribers for the current event?
            if (!this.subscribers.containsKey(event.getGame())) {
                return;
            }
            final Map<String, Set<String>> eventTypeMapping = this.subscribers.get(event.getGame());
            // do we have subscribers for the current event type?
            if (!eventTypeMapping.containsKey(event.getType())) {
                return;
            }
            final Set<String> eventSubscribers = eventTypeMapping.get(event.getType());
            final Gson gson = new Gson();
            // iterate through all subscribers
            for (final String targetUri : eventSubscribers) {
                try {
                    // send notification (we don't care about the response)
                    Unirest.post(targetUri+"/events").header("content-type", "application-json")
                            .body(gson.toJson(event)).asString();
                } catch (final Exception ex) {
                    System.out.println(ExceptionUtils.getExceptionInfo(ex, "NOTIFICATION"));
                }
            }
        };
    }

    /**
     * Adds subscribers by the given subscription data object.
     *
     * @param data
     *            Subscription data object used to add all matching
     *            subscriptions.
     */
    public synchronized void addSubscriber(final SubscriptionRegisterData data) {
        // create map for the current game (if required)
        if (!this.subscribers.containsKey(data.game)) {
            this.subscribers.put(data.game, new ConcurrentSkipListMap<>());
        }
        final Map<String, Set<String>> eventTypeMapping = this.subscribers.get(data.game);
        // iterate over all event types
        for (final String event : data.events) {
            // create list for the current event type (if required)
            if (!eventTypeMapping.containsKey(event)) {
                eventTypeMapping.put(event, new ConcurrentSkipListSet<>());
            }
            final Set<String> eventSubscribers = eventTypeMapping.get(event);
            // add uri to notification registry
            eventSubscribers.add(data.uri);
        }
    }

    /**
     * Returns an array containing information about all known subscribers.
     *
     * @return Array with information about all known subscribers.
     */
    public synchronized SubscriptionInformation[] getSubscribers() {
        final List<SubscriptionInformation> allSubscribers = new LinkedList<>();
        for (final Entry<String, Map<String, Set<String>>> gameSubscribers : this.subscribers
                .entrySet()) {
            final String game = gameSubscribers.getKey();
            for (final Entry<String, Set<String>> typeSubscribers : gameSubscribers.getValue()
                    .entrySet()) {
                final String type = typeSubscribers.getKey();
                for (final String current : typeSubscribers.getValue()) {
                    allSubscribers.add(new SubscriptionInformation(game, type, current));
                }
            }
        }
        return allSubscribers.toArray(new SubscriptionInformation[allSubscribers.size()]);
    }

    /**
     * Removes subscribers by the given subscription data object.
     *
     * @param data
     *            Subscription data object used to remove all matching
     *            subscriptions.
     */
    public synchronized void removeSubscriber(final SubscriptionRegisterData data) {
        // do we have subscribers for the current event?
        if (!this.subscribers.containsKey(data.game)) {
            return;
        }
        final Map<String, Set<String>> eventTypeMapping = this.subscribers.get(data.game);
        // iterate over all event types
        for (final String event : data.events) {
            // do we have subscribers for the current event type?
            if (!eventTypeMapping.containsKey(event)) {
                continue;
            }
            final Set<String> eventSubscribers = eventTypeMapping.get(event);
            // remove subscription from registry
            eventSubscribers.remove(data.uri);
            // remove set instance if the set is empty
            if (eventSubscribers.isEmpty()) {
                eventTypeMapping.remove(event);
            }
        }
        // remove game map if the game has no subscriptions
        if (eventTypeMapping.isEmpty()) {
            this.subscribers.remove(data.game);
        }
    }
}
