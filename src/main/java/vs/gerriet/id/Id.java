package vs.gerriet.id;

import spark.Request;

/**
 * Base class for IDs. This is basically just a container class for the given
 * internal ID type. Used to allow type checking on IDs with the same base type
 * (e.g. {@link String}).
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 *
 * @param <T>
 *            Internal ID type.
 */
public abstract class Id<T extends Comparable<T>> implements Comparable<Id<T>> {

    /**
     * Internal id.
     */
    private T data;

    /**
     * Creates a new ID container from the given request parameter.
     * 
     * @param request
     *            Request to load the parameter from.
     * @param param
     *            Parameter name containing the id.
     */
    public Id(final Request request, final String param) {
        this.setBaseData(this.fromUriSuffix(request.params(param)));
    }

    /**
     * Creates a new ID container from the given id.
     *
     * @param data
     *            Id data.
     */
    public Id(final T data) {
        this.setBaseData(data);
    }

    @Override
    public int compareTo(final Id<T> obj) {
        return this.data.compareTo(obj.data);
    }

    /**
     * Since we implement {@link Comparable}, we should also implement equals.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Id<?> other = (Id<?>) obj;
        if (this.data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!this.data.equals(other.data)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the internal id data.
     *
     * @return Internal id data.
     */
    public T getBaseData() {
        return this.data;
    }

    /**
     * Creates a uri from this id.
     *
     * @return Created uri.
     */
    public String getUri() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getUri());
        builder.append(this.data.toString());
        builder.append('/');
        return builder.toString();
    }

    /**
     * We override {@link #equals(Object)} so we also override
     * {@link #hashCode()}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
        return result;
    }

    /**
     * Loads this id instance from the given uri.
     *
     * @param uri
     *            Uri string.
     * @return <code>true</code> on success, <code>false</code> otherwise.
     */
    public boolean loadUri(final String uri) {
        final String prefix = this.getUriPrefix();
        if (!uri.startsWith(prefix)) {
            return false;
        }
        final String suffix = uri.replace(prefix, "");
        final T suffixData = this.fromUriSuffix(suffix);
        if (suffixData == null) {
            return false;
        }
        this.data = suffixData;
        return true;
    }

    /**
     * Setter for the internal base data.
     *
     * @param data
     *            New base data value.
     */
    public void setBaseData(final T data) {
        this.data = data;
    }

    /**
     * Simply calls {@link #getUri()}.
     */
    @Override
    public String toString() {
        return this.getUri();
    }

    /**
     * Parses the uri suffix into the internal data format.
     *
     * @param suffix
     *            Uri suffix.
     * @return Base data element or <code>null</code> if parsing failed.
     */
    protected abstract T fromUriSuffix(String suffix);

    /**
     * Returns the uri prefix (<i>with</i> trailing slash).
     *
     * @return Uri prefix.
     */
    protected abstract String getUriPrefix();
}
