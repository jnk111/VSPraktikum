package vs.gerriet.service;

import vs.gerriet.controller.event.SubscriptionListController;
import vs.gerriet.model.event.EventNotificationQueue;
import vs.gerriet.utils.ServiceUtils;
import vs.jonas.services.json.EventData;

/**
 * Class providing the event subscription system for the event service.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public class EventSubscriptionService {

    /**
     * Queue that send event notification asynchronous.
     */
    public static EventNotificationQueue notificationQueue;

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
        EventSubscriptionService.notificationQueue.push(event);
    }

    /**
     * Initializes required controllers for the event subscription system.
     */
    public static void run() {
        // initialize the notification queue first
        EventSubscriptionService.notificationQueue = new EventNotificationQueue();
        // register controller
        ServiceUtils.registerController(new SubscriptionListController());
    }
}
