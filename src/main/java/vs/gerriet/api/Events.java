package vs.gerriet.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.controller.event.SubscriptionListController;
import vs.gerriet.json.event.SubscriptionInformation;
import vs.gerriet.json.event.SubscriptionRegisterData;
import vs.jonas.services.json.EventData;

/**
 * Class for event service API calls.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class Events extends VsApiBase {

    /**
     * Adds subscription for the events specified by the given subscription
     * data.
     *
     * @param data
     *            Subscription data.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> addSubscription(final SubscriptionRegisterData data) {
        try {
            return Unirest.post("http://localhost" + SubscriptionListController.URI)
                    .header("content-type", "application/json").body(data).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Creates a new event on the event service.
     *
     * @param data
     *            Event data.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> createEvent(final EventData data) {
        try {
            return Unirest.post(this.getServiceUri() + "/events")
                    .header("content-type", "application/json").body(data).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Loads a list of all subscriptions known by the event service.
     *
     * @return List of all known subscriptions or <code>null</code> if the
     *         request failed.
     */
    public HttpResponse<SubscriptionInformation[]> getAllSubscriptions() {
        try {
            return Unirest.post(this.getServiceUri() + SubscriptionListController.URI)
                    .asObject(SubscriptionInformation[].class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    @Override
    public String getType() {
        return "events";
    }

    /**
     * Adds subscription for the events specified by the given subscription
     * data.
     *
     * @param data
     *            Subscription data.
     * @return Empty response or <code>null</code> if the request failed.
     */
    public HttpResponse<String> removeSubscription(final SubscriptionRegisterData data) {
        try {
            return Unirest.delete(this.getServiceUri() + SubscriptionListController.URI)
                    .header("content-type", "application/json").body(data).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }
}
