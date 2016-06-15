package vs.gerriet.id.event;

import spark.Request;
import vs.gerriet.controller.event.SubscriptionListController;
import vs.gerriet.id.Id;

/**
 * Subscription id container.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class SubscriptionId extends Id<Integer> {

    /**
     * Creates a new subscription id container.
     *
     * @param data
     *            Internal subscription id.
     */
    public SubscriptionId(final Integer data) {
        super(data);
    }

    /**
     * Creates a new subscription id container.
     *
     * @param request
     *            Request to load the id from.
     * @param param
     *            Parameter name containing the id.
     */
    public SubscriptionId(final Request request, final String param) {
        super(request, param);
    }

    @Override
    protected Integer fromUriSuffix(final String suffix) {
        return Integer.decode(suffix);
    }

    @Override
    protected String getUriPrefix() {
        return SubscriptionListController.URI + "/";
    }

}
