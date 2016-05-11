package vs.gerriet.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for id functions.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class IdUtils {

    /**
     * Counter for unique instance IDs.
     */
    private static long counter = 0;

    /**
     * <p>
     * Calculates id from uri.
     * </p>
     * <p>
     * If no uri is given, the initial string will be returned.
     * </p>
     *
     * @param uri
     *            Uri.
     * @return calculated id.
     */
    public static String getIdFromUri(final String uri) {
        final Pattern pattern = Pattern.compile(".*/([^/]*)/?$");
        final Matcher m = pattern.matcher(uri);
        if (m.matches()) {
            return m.group(0);
        }
        return uri.replace('/', '-');
    }

    /**
     * <p>
     * Returns a unique id for this running instance.
     * </p>
     * <p>
     * Other running instances will have the same IDs.
     * </p>
     *
     * @return unique id.
     */
    public static synchronized long getUniqueRunntimeId() {
        return IdUtils.counter++;
    }

    /**
     * Hide default constructor.
     */
    private IdUtils() {
        // hide default constructor.
    }
}
