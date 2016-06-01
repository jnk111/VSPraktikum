package vs.gerriet.api;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import vs.gerriet.exception.ApiException;
import vs.gerriet.id.Id;

/**
 * <p>
 * Lazy list where the content is loaded when an element of the list is accessed
 * the first time.
 * </p>
 * <p>
 * Note that all list operations might throw an {@link ApiException} if loading
 * fails.
 * </p>
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 *
 * @param <E>
 *            List element type.
 */
public interface LazyMap<K extends Id<?>, V> extends Map<K, V>, Lazy {

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default void clear() throws ApiException {
        this.load();
        this.getInternalMap().clear();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V compute(final K key,
            final BiFunction<? super K, ? super V, ? extends V> remappingFunction)
            throws ApiException {
        this.load();
        return this.getInternalMap().compute(key, remappingFunction);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction)
            throws ApiException {
        this.load();
        return this.getInternalMap().computeIfAbsent(key, mappingFunction);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V computeIfPresent(final K key,
            final BiFunction<? super K, ? super V, ? extends V> remappingFunction)
            throws ApiException {
        this.load();
        return this.getInternalMap().computeIfPresent(key, remappingFunction);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean containsKey(final Object key) throws ApiException {
        this.load();
        return this.containsKey(key);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean containsValue(final Object value) throws ApiException {
        this.load();
        return this.getInternalMap().containsValue(value);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default Set<Map.Entry<K, V>> entrySet() throws ApiException {
        this.load();
        return this.getInternalMap().entrySet();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default void forEach(final BiConsumer<? super K, ? super V> action) throws ApiException {
        this.load();
        this.getInternalMap().forEach(action);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V get(final Object key) throws ApiException {
        this.load();
        return this.getInternalMap().get(key);
    }

    /**
     * Returns the internal map for this lazy map.
     *
     * @return Map instance.
     */
    Map<K, V> getInternalMap();

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V getOrDefault(final Object key, final V defaultValue) throws ApiException {
        this.load();
        return this.getInternalMap().getOrDefault(key, defaultValue);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean isEmpty() throws ApiException {
        this.load();
        return this.getInternalMap().isEmpty();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default Set<K> keySet() throws ApiException {
        this.load();
        return this.getInternalMap().keySet();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V put(final K key, final V value) throws ApiException {
        this.load();
        return this.getInternalMap().put(key, value);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default void putAll(final Map<? extends K, ? extends V> map) throws ApiException {
        this.load();
        this.getInternalMap().putAll(map);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V putIfAbsent(final K key, final V value) throws ApiException {
        this.load();
        return this.getInternalMap().putIfAbsent(key, value);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V remove(final Object key) throws ApiException {
        this.load();
        return this.getInternalMap().remove(key);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean remove(final Object key, final Object value) throws ApiException {
        this.load();
        return this.getInternalMap().remove(key, value);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default V replace(final K key, final V value) throws ApiException {
        this.load();
        return this.getInternalMap().replace(key, value);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean replace(final K key, final V oldValue, final V newValue) throws ApiException {
        this.load();
        return this.getInternalMap().replace(key, oldValue, newValue);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function)
            throws ApiException {
        this.load();
        this.getInternalMap().replaceAll(function);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default int size() throws ApiException {
        this.load();
        return this.getInternalMap().size();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default Collection<V> values() throws ApiException {
        this.load();
        return this.getInternalMap().values();
    }
}
