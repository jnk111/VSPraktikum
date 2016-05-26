package vs.gerriet.utils;

import spark.Spark;
import vs.gerriet.controller.Controller;
import vs.gerriet.controller.Controller.DeleteController;
import vs.gerriet.controller.Controller.GetController;
import vs.gerriet.controller.Controller.PostController;
import vs.gerriet.controller.Controller.PutController;

public class ServiceUtils {

    /**
     * Registers the given controller into the spark web-server.
     *
     * @param controller
     *            Controller to be registered.
     */
    public static void registerController(final Controller controller) {
        if (controller instanceof GetController) {
            Spark.get(controller.getUri(), ((GetController) controller)::get);
            System.out.println(
                    "GET " + controller.getUri() + " -> " + controller.getClass().getName());
        }
        if (controller instanceof PostController) {
            Spark.post(controller.getUri(), ((PostController) controller)::post);
            System.out.println(
                    "POST " + controller.getUri() + " -> " + controller.getClass().getName());
        }
        if (controller instanceof PutController) {
            Spark.put(controller.getUri(), ((PutController) controller)::put);
            System.out.println(
                    "PUT " + controller.getUri() + " -> " + controller.getClass().getName());
        }
        if (controller instanceof DeleteController) {
            Spark.delete(controller.getUri(), ((DeleteController) controller)::delete);
            System.out.println(
                    "DELETE " + controller.getUri() + " -> " + controller.getClass().getName());
        }
    }

    /**
     * Hide default constructor.
     */
    private ServiceUtils() {
        // hide default constructor
    }
}
