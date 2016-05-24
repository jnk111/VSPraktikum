package vs.jan.api.userservice;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import vs.jan.exception.InvalidInputException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.model.StatusCodes;
import vs.jan.model.User;
import vs.jan.services.userservice.UserService;

public class UserServiceRESTApi {

	private final String CLRF = "\r" + "\n";
	private final UserService SERVICE = new UserService();
	private final Gson GSON = new Gson();

	public UserServiceRESTApi() {
		initGET();
		initPOST();
		initPUT();
		initDELETE();
		initExceptions();
	}

	private void initGET() {
		initGETUserlist();
		initGETUserById();
	}

	/**
	 * Eine spezifischen User als JSON zuueckgeben
	 */
	private void initGETUserById() {
		get("/users/:userid", "application/json", (req, resp) -> {
			User u = SERVICE.getSpecificUser(req.pathInfo());
			resp.status(StatusCodes.SUCCESS);
			return GSON.toJson(u);
		});
	}

	/**
	 * Die gesamte Userliste der aktiv angemeldeten User als JSON zurueckgeben
	 */
	private void initGETUserlist() {
		get("/users", "application/json", (req, resp) -> {

			List<String> userIds = SERVICE.getUserIds();
			resp.status(StatusCodes.SUCCESS);
			return GSON.toJson(userIds);
		});
	}

	/**
	 * Neuen User eintragen, Uebergabe als JSON im Request-Body
	 */
	private void initPOST() {
		post("/users", "application/json", (req, resp) -> {
			User user = new Gson().fromJson(req.body(), User.class); // Mapping JSON
																																// -> User
			SERVICE.createUser(user);
			resp.status(StatusCodes.CREATED);
			return StatusCodes.CREATED + CLRF;
		});
	}

	/**
	 * Veraendert einen User-Eintrag
	 */
	private void initPUT() {

		put("/users/:userid", (req, resp) -> {
			String name = req.queryParams("name");
			String uri = req.queryParams("uri");
			SERVICE.updateUser(req.pathInfo(), name, uri);
			resp.status(StatusCodes.SUCCESS);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	/**
	 * Loescht einen User aus der Map, identifiziert mit dem gesamten Pfad
	 */
	private void initDELETE() {

		delete("/users/:userid", (req, resp) -> {

			SERVICE.deleteUser(req.pathInfo());
			resp.status(StatusCodes.SUCCESS);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	private void initExceptions() {

		exception(JsonSyntaxException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": Invalid Json Input!");
			exception.printStackTrace();
		});

		exception(ResourceNotFoundException.class, (exception, request, response) -> {

			response.status(StatusCodes.NOT_FOUND);
			response.body(StatusCodes.NOT_FOUND + ": Resource not Found!");
			exception.printStackTrace();
		});

		exception(InvalidInputException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ":Invalid Query-Params!");
			exception.printStackTrace();
		});
	}
}
