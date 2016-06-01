package vs.gerriet.api;

import vs.gerriet.exception.ApiException;

/**
 * Base interface for lazy API components.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public interface Lazy {
    /**
     * <p>
     * Loads the contents of this lazy object.
     * </p>
     * <p>
     * Only the first call to this method will load data, following calls of
     * this method will do nothing at all.
     * </p>
     *
     * @throws ApiException
     *             If loading failed.
     */
    void load() throws ApiException;

    /**
     * <p>
     * Refreshes this lazy object and forces a reload of its contents.
     * </p>
     * <p>
     * This will always result in a reload of the data for this object.
     * </p>
     *
     * @throws ApiException
     *             If loading failed.
     */
    void refresh() throws ApiException;
}
