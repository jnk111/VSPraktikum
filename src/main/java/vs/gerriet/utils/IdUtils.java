package vs.gerriet.utils;

/**
 * Utility class for id functions.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class IdUtils {

    /**
     * Counter for unique instance IDs.
     */
    private static int counter = 0;

    /**
     * <p>
     * Returns a unique id for this running instance.
     * </p>
     * <p>
     * Other running instances will generate the same IDs.
     * </p>
     *
     * @return unique id.
     */
    public static synchronized int getUniqueRunntimeId() {
        return IdUtils.counter++;
    }

    /**
     * Hide default constructor.
     */
    private IdUtils() {
        // hide default constructor.
    }
}
