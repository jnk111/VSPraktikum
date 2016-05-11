package vs.gerriet.controller;

import spark.Request;
import spark.Response;

/**
 * Interface for controllers.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public interface Controller {

    /**
     * Interface for controllers that provide a DELETE handler.
     *
     * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
     */
    public interface DeleteController extends Controller {
        /**
         * Handler method for DELETE requests.
         *
         * @param request
         *            Sent request.
         * @param response
         *            Response instance.
         * @return Response body.
         */
        public String delete(Request request, Response response);
    }

    /**
     * Interface for controllers that provide a GET handler.
     *
     * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
     */
    public interface GetController extends Controller {
        /**
         * Handler method for GET requests.
         *
         * @param request
         *            Sent request.
         * @param response
         *            Response instance.
         * @return Response body.
         */
        public String get(Request request, Response response);
    }

    /**
     * Interface for controllers that provide a POST handler.
     *
     * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
     */
    public interface PostController extends Controller {
        /**
         * Handler method for POST requests.
         *
         * @param request
         *            Sent request.
         * @param response
         *            Response instance.
         * @return Response body.
         */
        public String post(Request request, Response response);
    }

    /**
     * Interface for controllers that provide a PUT handler.
     *
     * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
     */
    public interface PutController extends Controller {
        /**
         * Handler method for PUT requests.
         *
         * @param request
         *            Sent request.
         * @param response
         *            Response instance.
         * @return Response body.
         */
        public String put(Request request, Response response);
    }

    /**
     * Mime type for JSON data.
     */
    public static final String MIME_TYPE_JSON = "application/json";

    /**
     * Returns the uri for this controller.
     * 
     * @return Controller uri.
     */
    public String getUri();
}
