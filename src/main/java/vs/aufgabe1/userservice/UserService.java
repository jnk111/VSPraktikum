package vs.aufgabe1.userservice;
import static spark.Spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.aufgabe1.StatusCodes;

public class UserService{

	private final String CLRF = "\r" + "\n";
	
	private static Map<String, User> users = new HashMap<>(); // Mapping id auf User
	
	public UserService(){
		initGET();		
		initPOST();
		initPUT();
		initDELETE();
	}

	/**
	 * Loescht einen User aus der Map, identifiziert mit dem gesamten Pfad
	 */
	private synchronized void initDELETE() {
		
		delete("/users/:userid", (req, resp) -> {
			
			boolean erfolg = false;
			String id = req.pathInfo(); // Gesamten Pfad nehmen, da dies der Key in der Map ist
			User user = users.get(id);
			
			if(user != null){
				users.remove(id);
				resp.status(StatusCodes.SUCCESS);
				erfolg = true;
			}else{
				resp.status(StatusCodes.BAD_REQ);
			}
			return "" + erfolg + CLRF;
		});
	}

	
	/**
	 * Veraendert einen User-Eintrag
	 */
	private synchronized void initPUT() {
		
		put("/users/:userid", (req, resp) -> {
			
			String id = req.pathInfo(); 	// Gesamten Pfad nehmen, da dies der Key in der Map ist
			String name = req.queryParams("name");
			String uri = req.queryParams("uri");
			boolean erfolg = false;
			User user = users.get(id);
			if(user != null
					&& user.isValid()){
				user.setName(name);
				user.setUri(uri);
				resp.status(StatusCodes.SUCCESS);
				erfolg = true;
			}else{
				resp.status(StatusCodes.BAD_REQ);
			}
			
			return "" + erfolg + CLRF;
		});
		
	}

	/**
	 * Neuen User eintragen, Uebergabe als JSON im Request-Body
	 */
	private synchronized void initPOST() {
		post("/users", "application/json", (req, resp) -> {
			User user = new Gson().fromJson(req.body(), User.class); // Mapping JSON -> User
			boolean erfolg = false;
			
			if(user != null
					&& user.isValid()){ 	// JSON richtig konvertiert, hat also Klient Schnittstelle eingehalten?
				users.put(user.getId(), user);
				resp.status(StatusCodes.SUCCESS);
				erfolg = true;
			}else{
				resp.status(StatusCodes.BAD_REQ); // BAD_REQUEST
			}
			return "" + erfolg + CLRF; // Oder etwas anders zurueckgeben? void geht nicht
		});
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

			String id = req.pathInfo();
			User user = users.get(id);
			String response = null;
			if(user != null){
				response = new Gson().toJson(user);
				resp.status(StatusCodes.SUCCESS);
			}else{
				response = StatusCodes.BAD_REQ + ": Not Found";
			}
			return response;
		});
		
	}

	/**
	 * Die gesamte Userliste der aktiv angemeldeten User als JSON zurueckgeben
	 */
	private void initGETUserlist() {
		get("/users", "application/json", (req, resp) -> {
			List<String> userIds = getUserIds();
			String response = null;
			if(!userIds.isEmpty()){
				resp.status(StatusCodes.SUCCESS);
				response = new Gson().toJson(userIds);
			}else{
				resp.status(StatusCodes.BAD_REQ); // BAD_REQUEST
				response = StatusCodes.BAD_REQ + ": No Users Available";
			}
			return response;
		});
		
	}

	/**
	 * Erstellt eine Liste der User-IDs
	 * @return 
	 */
	private static List<String> getUserIds() {
		
		List<String> userIds = new ArrayList<>();
		users.forEach((key, value) -> userIds.add(key)); // Andere Notation fuer 
																										// For-Each-Loop (seit Java 1.8)
		return userIds;
	}
}
