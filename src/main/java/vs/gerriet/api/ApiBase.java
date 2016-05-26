package vs.gerriet.api;

import com.google.gson.Gson;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

/**
 * Base API class.
 *
 * @author Gerriet Hinrichs {@literal <gerriet.hinrichs@web.de>}
 */
public abstract class ApiBase {

    /**
     * Initialize object mapper for unirest.
     */
    static {
        Unirest.setObjectMapper(new ObjectMapper() {
            /**
             * Gson instance for marshaling.
             */
            private final Gson gson = new Gson();

            @Override
            public <T> T readValue(final String value, final Class<T> valueType) {
                return this.gson.fromJson(value, valueType);
            }

            @Override
            public String writeValue(final Object value) {
                return this.gson.toJson(value);
            }
        });
    }

    /**
     * Returns the service uri.
     *
     * @return Service uri.
     */
    public abstract String getServiceUri();
}
