package vs.aufgabe1b.services;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import vs.aufgabe1b.interfaces.EventDAO;
import vs.aufgabe1b.models.Event;
import vs.aufgabe1b.models.factories.EventDAOFactory;
import vs.aufgabe1b.models.responses.EventList;

public class EventService {

	private final String EVENTS_BASE = "/events";
	private final String EVENT_ID = "/:eventid";
	public final String CONTENT_TYPE = "application/json;charset=utf-8";
	
	private static Gson gson;
	
	public EventService(){
		gson = new Gson();
	}
	
	/**
	 * Startet den Service.
	 * 
	 * Post: Erstellen von Events.
	 * Get: Abfragen der vorhandenen Events
	 * delete: löschen von Events
	 */
	public void startService(){
		
		// Create new Events
		post(EVENTS_BASE, (req,res) -> {
			try{
				EventDAO dao = EventDAOFactory.getDAO();
				Event newEvent = gson.fromJson(req.body(), Event.class);
				dao.createEvent(newEvent);
				res.status(HttpStatus.OK_200);				
			} catch (JsonSyntaxException ex){
				System.err.println("Fehler: Der Content im Body der empfangenen Nachricht entspricht nicht dem gültigen Format.");
				res.status(HttpStatus.BAD_REQUEST_400);
			}
			return "";
		});
		
		// Get Events By REGEX
		get(EVENTS_BASE, (req,res) -> {
			EventDAO dao = EventDAOFactory.getDAO();
			
			System.out.println("Get Events wird ausgeführt...");
			
			// Get Query-Params
			String game = req.queryParams("game");
			String type = req.queryParams("type");
			String name = req.queryParams("name");
			String reason = req.queryParams("reason");
			String ressource = req.queryParams("ressource");
			String player = req.queryParams("player");
			
			// Create Filter-Map
			Map<String,String> filter = new HashMap<>();
					
			// Add Filter
			if( game != null && !game.equals(""))filter.put("game",game);
			if(type != null && !type.equals(""))filter.put("type",type); 			
			if(name != null && !name.equals("")) filter.put("name", name);
			if(reason != null && !reason.equals("")) filter.put("reason", reason);
			if(ressource != null && !ressource.equals("")) filter.put("ressource", ressource);
			if(player != null && !player.equals("")) filter.put("player", player);
			
			List<Event> events = dao.findAllEventsByRegex(filter);	
			res.status(HttpStatus.OK_200);
			res.type(CONTENT_TYPE);
			// Crossidescripting Angriff JSON Array verhindern:
			return new EventList(events); 
		}, gson::toJson);
		
		
		// Delete Events.
		delete(EVENTS_BASE, (req,res) ->{
			EventDAO dao = EventDAOFactory.getDAO();
			
			System.out.println("Delete Events wird ausgeführt...");
			
			// Get Query-Params
			String game = req.queryParams("game");
			String type = req.queryParams("type");
			String name = req.queryParams("name");
			String reason = req.queryParams("reason");
			String ressource = req.queryParams("ressource");
			String player = req.queryParams("player");
			
			// Create Filter-Map
			Map<String,String> filter = new HashMap<>();
					
			// Add Filter
			if( game != null && !game.equals(""))filter.put("game",game);
			if(type != null && !type.equals(""))filter.put("type",type); 			
			if(name != null && !name.equals("")) filter.put("name", name);
			if(reason != null && !reason.equals("")) filter.put("reason", reason);
			if(ressource != null && !ressource.equals("")) filter.put("ressource", ressource);
			if(player != null && !player.equals("")) filter.put("player", player);
			
			dao.deleteAllEventsByRegex(filter);
			
			res.status(HttpStatus.OK_200);
			return "";
		});
		
		// GET State of Player Ressource
		get(EVENTS_BASE+EVENT_ID, (req,res) -> {
			EventDAO dao = EventDAOFactory.getDAO();
			Event event = dao.findByID(req.params("eventid"));
			System.out.println(event);
			res.type(CONTENT_TYPE);
			res.status(HttpStatus.OK_200);
			return event;
		},gson::toJson);
	}
	
	public static void main(String[] args) {
		System.out.println("*** Start EventService ***");
		new EventService().startService();
	}
}
