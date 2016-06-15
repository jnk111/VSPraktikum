package vs.gerriet.controller.event;

import spark.Request;
import spark.Response;
import vs.gerriet.controller.AbstractController;
import vs.gerriet.controller.Controller.DeleteController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.json.event.SubscriptionRegisterData;
import vs.gerriet.service.EventSubscriptionService;

/**
 * Controller class for event subscription list access.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class SubscriptionListController extends AbstractController
        implements GetController, PostController, DeleteController {

    /**
     * Base Uri for the event subscriptions.
     */
    public static final String URI = "/events/subscriptions";

    /**
     * Removes subscriptions.
     */
    @Override
    public String delete(final Request request, final Response response) {
        final SubscriptionRegisterData data =
                this.gson.fromJson(request.body(), SubscriptionRegisterData.class);
        // we need a game uri
        if (data.game == null || data.game.isEmpty()) {
            response.status(400);
            return "";
        }
        // we need a target uri
        if (data.uri == null || data.uri.isEmpty()) {
            response.status(400);
            return "";
        }
        // we need event types (allowed to be empty)
        if (data.events == null) {
            response.status(400);
            return "";
        }
        // we only perform the operation if the event list is not empty
        if (data.events.length != 0) {
            EventSubscriptionService.notificationQueue.removeSubscriber(data);
        }
        return "";
    }

    /**
     * Returns subscription data.
     */
    @Override
    public String get(final Request request, final Response response) {
        return this.gson.toJson(EventSubscriptionService.notificationQueue.getSubscribers());
    }

    @Override
    public String getUri() {
        return SubscriptionListController.URI;
    }

    /**
     * Adds subscriptions.
     */
    @Override
    public String post(final Request request, final Response response) {
        final SubscriptionRegisterData data =
                this.gson.fromJson(request.body(), SubscriptionRegisterData.class);
        // we need a game uri
        if (data.game == null || data.game.isEmpty()) {
            response.status(400);
            return "";
        }
        // we need a target uri
        if (data.uri == null || data.uri.isEmpty()) {
            response.status(400);
            return "";
        }
        // we need event types (allowed to be empty)
        if (data.events == null) {
            response.status(400);
            return "";
        }
        // we only perform the operation if the event list is not empty
        if (data.events.length != 0) {
            EventSubscriptionService.notificationQueue.addSubscriber(data);
        }
        return "";
    }
}
