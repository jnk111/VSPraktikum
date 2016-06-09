package vs.gerriet.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.stuff42.error.ExceptionUtils;
import vs.jonas.services.json.EventData;

public class Events extends VsApiBase {

    /**
     * Creates a new event on the event service.
     *
     * @param data
     *            Event data.
     * @return Empty response.
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

    @Override
    public String getType() {
        return "events";
    }
}
