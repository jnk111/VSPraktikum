package vs.gerriet.api;

import java.util.function.Function;

import com.mashape.unirest.http.HttpResponse;

import vs.gerriet.json.yellowpages.ServiceStatus;
import vs.gerriet.json.yellowpages.ServiceStatusList;

/**
 * Base class for VS API classes.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public abstract class VsApiBase extends ApiBase {

    /**
     * Group name.
     */
    public static final String GROUP_NAME = "JJMG";

    /**
     * Cache for yellow pages service list.
     */
    private static ServiceStatusList serviceCache;

    /**
     * Contains the cached uri for this service.
     */
    private String uriCache;

    /**
     * Yellow pages instance for url lookup.
     */
    protected YellowPages yellowPages = new YellowPages();

    /**
     * Returns the group name used for uri lookup.
     *
     * @return Group name.
     */
    @SuppressWarnings("static-method")
    public String getGroupName() {
        return VsApiBase.GROUP_NAME;
    }

    /**
     * <p>
     * Loads the url for the service from the yellow pages service.
     * </p>
     * <p>
     * May return <code>null</code> if the service url could not be loaded from
     * yellow pages service.
     * </p>
     */
    @Override
    public String getServiceUri() {
        if (this.uriCache == null) {
            this.uriCache = this.loadUriForService(this.getGroupName(), this.getType());
        }
        return this.uriCache;
    }

    /**
     * Returns the type of this service.
     *
     * @return Service type.
     */
    public abstract String getType();

    /**
     * Resets the internal data cache.
     */
    public void resetCache() {
        this.uriCache = null;
        VsApiBase.serviceCache = null;
    }

    /**
     * Loads the uri of the service that first matches the given filter
     *
     * @param filter
     *            Filter function.
     * @return Service uri. Returns <code>null</code> if loading failed.
     */
    protected String loadUriForService(final Function<ServiceStatus, Boolean> filter) {
        if (VsApiBase.serviceCache == null) {
            final HttpResponse<ServiceStatusList> response = this.yellowPages.getFullServices();
            if (response == null || response.getStatus() != 200) {
                return null;
            }
        }
        for (final ServiceStatus current : VsApiBase.serviceCache.services) {
            if (filter.apply(current).booleanValue()) {
                return current.uri;
            }
        }
        return null;
    }

    /**
     * Loads the uri of the service with the given type from the given group.
     *
     * @param groupName
     *            Group name.
     * @param type
     *            Service type.
     * @return Service uri. Returns <code>null</code> if loading failed.
     */
    protected String loadUriForService(final String groupName, final String type) {
        return this.loadUriForService((element) -> {
            // TODO @gerriet-hinrichs: check for status field?
            return Boolean.valueOf(element.name.equals(groupName) && element.service.equals(type));
        });
    }
}
