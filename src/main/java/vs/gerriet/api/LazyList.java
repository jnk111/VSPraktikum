package vs.gerriet.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import vs.gerriet.exception.ApiException;

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
public interface LazyList<E> extends List<E>, Lazy {
    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean add(final E element) throws ApiException {
        this.load();
        return this.getInternalList().add(element);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default void add(final int index, final E element) throws ApiException {
        this.load();
        this.getInternalList().add(index, element);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean addAll(final Collection<? extends E> collection) throws ApiException {
        this.load();
        return this.getInternalList().addAll(collection);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean addAll(final int index, final Collection<? extends E> collection)
            throws ApiException {
        this.load();
        return this.getInternalList().addAll(index, collection);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default void clear() throws ApiException {
        this.load();
        this.getInternalList().clear();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean contains(final Object object) throws ApiException {
        this.load();
        return this.getInternalList().contains(object);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean containsAll(final Collection<?> collection) throws ApiException {
        this.load();
        return this.getInternalList().containsAll(collection);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default E get(final int index) throws ApiException {
        this.load();
        return this.getInternalList().get(index);
    }

    /**
     * <p>
     * <b>Do not use this method yourself, use this instance's methods.</b>
     * </p>
     * <p>
     * Returns the internal list.
     * </p>
     * <p>
     * This list is only used as container for the returned internal list.
     * </p>
     * <p>
     * This method might return <code>null</code> if {@link #load()} has
     * not been called yet.
     * </p>
     *
     * @return The internal list.
     */
    List<E> getInternalList();

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default int indexOf(final Object object) throws ApiException {
        this.load();
        return this.getInternalList().indexOf(object);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean isEmpty() throws ApiException {
        this.load();
        return this.getInternalList().isEmpty();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default Iterator<E> iterator() throws ApiException {
        this.load();
        return this.getInternalList().iterator();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default int lastIndexOf(final Object object) throws ApiException {
        this.load();
        return this.getInternalList().lastIndexOf(object);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default ListIterator<E> listIterator() throws ApiException {
        this.load();
        return this.getInternalList().listIterator();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default ListIterator<E> listIterator(final int index) throws ApiException {
        this.load();
        return this.getInternalList().listIterator(index);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default E remove(final int index) throws ApiException {
        this.load();
        return this.getInternalList().remove(index);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean remove(final Object object) throws ApiException {
        this.load();
        return this.getInternalList().remove(object);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean removeAll(final Collection<?> collection) throws ApiException {
        this.load();
        return this.getInternalList().removeAll(collection);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default boolean retainAll(final Collection<?> collection) throws ApiException {
        this.load();
        return this.getInternalList().retainAll(collection);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default E set(final int index, final E element) throws ApiException {
        this.load();
        return this.getInternalList().set(index, element);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default int size() throws ApiException {
        this.load();
        return this.getInternalList().size();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default List<E> subList(final int fromIndex, final int toIndex) throws ApiException {
        this.load();
        return this.getInternalList().subList(fromIndex, toIndex);
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default Object[] toArray() throws ApiException {
        this.load();
        return this.getInternalList().toArray();
    }

    /**
     * @throws ApiException
     *             If lazy loading failed.
     * @see #load()
     */
    @Override
    default <T> T[] toArray(final T[] array) throws ApiException {
        this.load();
        return this.getInternalList().toArray(array);
    }
}
