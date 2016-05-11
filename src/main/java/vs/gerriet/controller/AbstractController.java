package vs.gerriet.controller;

import com.google.gson.Gson;

/**
 * Base class for controllers.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public abstract class AbstractController implements Controller {
    /**
     * {@link Gson} instance to read and write json data.
     */
    protected Gson gson = new Gson();
}
