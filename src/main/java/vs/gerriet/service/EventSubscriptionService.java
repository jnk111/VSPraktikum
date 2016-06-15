package vs.gerriet.service;

import vs.jonas.services.json.EventData;

/**
 * Class providing the event subscription system for the event service.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class EventSubscriptionService {

    /**
     * <p>
     * Will initialize notifications being sent for the given event.
     * </p>
     * <p>
     * Sending the notifications will take part in a different thread.
     * </p>
     * <p>
     * This is a non blocking method.
     * </p>
     *
     * @param event
     *            Data object of the event subscribers should receive
     *            notifications for.
     */
    public static void notifySubscribers(final EventData event) {
        // TODO @gerriet-hinrichs: implement
    }

    /**
     * Initializes required controllers for the event subscription system.
     */
    public static void run() {
        // TODO @gerriet-hinrichs: implement
    }
}
