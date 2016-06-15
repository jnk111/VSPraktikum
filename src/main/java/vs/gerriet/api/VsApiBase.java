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
     * Contains the cached uri for this service.
     */
    private String uriCache;
    /**
     * Yellow pages instance for uri lookup.
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
     * Loads the i for the service from the yellow pages service.
     * </p>
     * <p>
     * May return <code>null</code> if the service uri could not be loaded from
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
    }

    /**
     * Resets the set service uri.
     */
    public void resetServiceUri() {
        this.setServiceUri(null);
    }

    /**
     * <p>
     * Sets the uri for this API class to the given uri.
     * </p>
     * <p>
     * Use for debug only.
     * </p>
     *
     * @param uri
     *            New service uri.
     */
    public void setServiceUri(final String uri) {
        this.uriCache = uri;
    }

    /**
     * Loads the uri of the service that first matches the given filter.
     *
     * @param filter
     *            Filter function.
     * @return Service uri. Returns <code>null</code> if loading failed.
     */
    protected String loadUriForService(final Function<ServiceStatus, Boolean> filter) {
        final HttpResponse<ServiceStatusList> response = this.yellowPages.getFullServices();
        if (response == null || response.getStatus() != 200) {
            return null;
        }
        for (final ServiceStatus current : response.getBody().services) {
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
            return Boolean.valueOf(element.name.equals(groupName) && element.service.equals(type)
                    && !element.status.equals("dead"));
        });
    }
}
