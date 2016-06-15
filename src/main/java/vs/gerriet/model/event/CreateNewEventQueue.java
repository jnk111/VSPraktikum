package vs.gerriet.model.event;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.api.Events;
import vs.jonas.services.json.EventData;

/**
 * Queue to asynchronous add events to the given event service.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class CreateNewEventQueue extends AsyncConsumerQueue<EventData> {

    /**
     * Event API instance used to create events.
     */
    private final Events eventApi = new Events();

    /**
     * Creates a new queue. The internal consumer will create a new event from
     * the added event data.
     */
    public CreateNewEventQueue() {
        super();
        // we use the parent constructor without arguments so we can reuse the
        // API instance
        this.consumer = event -> {
            try {
                // we only try once and drop the event if the request failed
                this.eventApi.createEvent(event);
            } catch (final Exception ex) {
                System.out.println(ExceptionUtils.getExceptionInfo(ex, "EVENT"));
            }
        };
    }

}
