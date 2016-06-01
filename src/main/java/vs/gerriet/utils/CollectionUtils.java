package vs.gerriet.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Utility class for collection related stuff.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class CollectionUtils {
    /**
     * Maps the given list to an array.
     *
     * @param list
     *            Input list.
     * @param generator
     *            Generator for the target list.
     * @param function
     *            Function to be mapped.
     * @return Target list after applying the given function to all elements.
     */
    public static <T, R> R[] arrayMap(final List<T> list, final IntFunction<R[]> generator,
            final Function<T, R> function) {
        return list.stream().map(function).toArray(generator);
    }

    /**
     * Maps the given array to an array.
     *
     * @param array
     *            Input array.
     * @param generator
     *            Generator for the target list.
     * @param function
     *            Function to be mapped.
     * @return Target list after applying the given function to all elements.
     */
    public static <T, R> R[] arrayMap(final T[] array, final IntFunction<R[]> generator,
            final Function<T, R> function) {
        return Arrays.stream(array).map(function).toArray(generator);
    }

    /**
     * Maps the given list to an array. Operations are performed parallel.
     *
     * @param list
     *            Input list.
     * @param generator
     *            Generator for the target list.
     * @param function
     *            Function to be mapped.
     * @return Target list after applying the given function to all elements.
     */
    public static <T, R> R[] arrayMapParallel(final List<T> list, final IntFunction<R[]> generator,
            final Function<T, R> function) {
        return list.parallelStream().map(function).toArray(generator);
    }

    /**
     * Maps the given array to an array. Operations are performed parallel.
     *
     * @param array
     *            Input array.
     * @param generator
     *            Generator for the target list.
     * @param function
     *            Function to be mapped.
     * @return Target list after applying the given function to all elements.
     */
    public static <T, R> R[] arrayMapParallel(final T[] array, final IntFunction<R[]> generator,
            final Function<T, R> function) {
        return Arrays.stream(array).parallel().map(function).toArray(generator);
    }

    /**
     * Maps the given list to a list.
     *
     * @param list
     *            Input list.
     * @param function
     *            Function to be mapped.
     * @return Target list.
     */
    public static <T, R> List<R> listMap(final List<T> list, final Function<T, R> function) {
        return list.stream().map(function).collect(Collectors.toList());
    }

    /**
     * Maps the given array to a list.
     *
     * @param array
     *            Input array.
     * @param function
     *            Function to be mapped.
     * @return Target list.
     */
    public static <T, R> List<R> listMap(final T[] array, final Function<T, R> function) {
        return Arrays.stream(array).map(function).collect(Collectors.toList());
    }

    /**
     * Maps the given list to a list. Operations are performed parallel.
     *
     * @param list
     *            Input list.
     * @param function
     *            Function to be mapped.
     * @return Target list.
     */
    public static <T, R> List<R> listMapParallel(final List<T> list,
            final Function<T, R> function) {
        return list.parallelStream().map(function).collect(Collectors.toList());
    }

    /**
     * Maps the given array to a list. Operations are performed parallel.
     *
     * @param array
     *            Input array.
     * @param function
     *            Function to be mapped.
     * @return Target list.
     */
    public static <T, R> List<R> listMapParallel(final T[] array, final Function<T, R> function) {
        return Arrays.stream(array).parallel().map(function).collect(Collectors.toList());
    }

    /**
     * Hide default constructor.
     */
    private CollectionUtils() {
        // Hide default constructor.
    }
}
