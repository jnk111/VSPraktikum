package vs.gerriet.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.stuff42.error.ExceptionUtils;
import vs.gerriet.id.ServiceId;
import vs.gerriet.json.yellowpages.Service;
import vs.gerriet.json.yellowpages.ServiceList;
import vs.gerriet.json.yellowpages.ServiceStatus;
import vs.gerriet.json.yellowpages.ServiceStatusList;

/**
 * API class for the yellow pages service.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class YellowPages extends ApiBase {

    /**
     * Yellow pages uri.
     */
    public static final String URI = "172.18.0.5:4567";

    /**
     * Uri for the service list.
     */
    public static final String LIST_URI = "/services";
    /**
     * Uri for the service list by name.
     */
    public static final String LIST_BY_NAME_URI = "/services/of/name/";

    /**
     * Uri for the service list by name.
     */
    public static final String LIST_BY_TYPE_URI = "/services/of/type/";

    /**
     * Loads a list with all known services with service details.
     *
     * @return Response with service list also containing service details.
     *         Returns <code>null</code> on error.
     */
    public HttpResponse<ServiceStatusList> getFullServices() {
        try {
            return Unirest.get(this.getServiceUri() + YellowPages.LIST_URI)
                    .queryString("extended", "true").asObject(ServiceStatusList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Loads information about the service with the given id.
     *
     * @param id
     *            Service id.
     * @return Response with service information. Returns <code>null</code> on
     *         error.
     */
    public HttpResponse<ServiceStatus> getService(final ServiceId id) {
        try {
            return Unirest.get(this.getServiceUri() + id.getUri()).asObject(ServiceStatus.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Loads a list with all known services.
     *
     * @return Response with service list. Returns <code>null</code> on error.
     */
    public HttpResponse<ServiceList> getServices() {
        try {
            return Unirest.get(this.getServiceUri() + YellowPages.LIST_URI)
                    .asObject(ServiceList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Loads a list with all services that have the given name.
     *
     * @param name
     *            Service name.
     * @return Response with service list. Returns <code>null</code> on error.
     */
    public HttpResponse<ServiceList> getServicesByName(final String name) {
        try {
            return Unirest.get(this.getServiceUri() + YellowPages.LIST_BY_NAME_URI + name)
                    .asObject(ServiceList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Loads a list with all services that have the given type.
     *
     * @param type
     *            Service type.
     * @return Response with service list. Returns <code>null</code> on error.
     */
    public HttpResponse<ServiceList> getServicesByType(final String type) {
        try {
            return Unirest.get(this.getServiceUri() + YellowPages.LIST_BY_TYPE_URI + type)
                    .asObject(ServiceList.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    @Override
    public String getServiceUri() {
        return YellowPages.URI;
    }

    /**
     * Registers the service.
     *
     * @param service
     *            Service to be registered. Returns <code>null</code> on error.
     * @return Empty response.
     */
    public HttpResponse<String> registerService(final Service service) {
        try {
            return Unirest.post(this.getServiceUri() + YellowPages.LIST_URI).body(service)
                    .asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Unregisters the service with the given id.
     *
     * @param id
     *            Service id.
     * @return Empty response. Returns <code>null</code> on error.
     */
    public HttpResponse<String> unregisterService(final ServiceId id) {
        try {
            return Unirest.delete(this.getServiceUri() + id.getUri()).asString();
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }

    /**
     * Updates the service.
     *
     * @param id
     *            Id of the service to update.
     * @param service
     *            New data for the service.
     * @return Empty Response. Returns <code>null</code> on error.
     */
    public HttpResponse<String> updateService(final ServiceId id, final Service service) {
        try {
            return Unirest.put(this.getServiceUri() + id.getUri()).body(service)
                    .asObject(String.class);
        } catch (final UnirestException ex) {
            System.err.println(ExceptionUtils.getExceptionInfo(ex, "API"));
            return null;
        }
    }
}
