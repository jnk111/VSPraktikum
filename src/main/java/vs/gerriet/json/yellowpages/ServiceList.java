package vs.gerriet.json.yellowpages;

import java.util.LinkedList;
import java.util.List;

import vs.gerriet.id.ServiceId;

/**
 * JSON data object for the service list.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class ServiceList {
    /**
     * Contains service uris.
     */
    public String[] services;

    /**
     * Creates a new service list.
     *
     * @param services
     *            Service uris.
     */
    public ServiceList(final String[] services) {
        this.services = services;
    }

    /**
     * Creates a list of service id objects from this service list.
     * 
     * @return List of service id objects.
     */
    public List<ServiceId> asServiceIdList() {
        final List<ServiceId> list = new LinkedList<>();
        for (final String current : this.services) {
            final ServiceId id = new ServiceId(null);
            id.loadUri(current);
            list.add(id);
        }
        return list;
    }
}
