package vs.jonas.services.model.dao;

import java.util.List;
import java.util.Map;

import vs.jonas.services.json.EventData;
import vs.jonas.services.model.Event;

public interface EventDAO {

	/**
	 * Liefert alle vorhandenen Events.
	 */
	public List<Event> getEvents();
	
	/**
	 * Liefert alle vorhandenen Events anhand von regul�ren Ausdr�cken.
	 * 
	 * @param filter Enth�lt die geforderten Filter.
	 * 
	 * Beispiel: Werden zum Beispiel Events vom Spiel "Monopoly" gesucht,
	 * so muss in der Map der Filtertyp "game" als Key angegeben werden mit dem value "Monopoly".
	 * 
	 * Vielleicht �ndere ich das bei Zeit nochmal in einen eigenen Enumtyp um. ;)
	 */
	public List<Event> findAllEventsByRegex(Map<String,String> filter);

	/**
	
	 * L�scht alle Events, die dem regul�ren Ausdruck entsprechen.
	 * 
	 * @param filter Enth�lt die geforderten Filter.
	 * 
	 * Beispiel: Werden zum Beispiel Events vom Spiel "Monopoly" gesucht,
	 * so muss in der Map der Filtertyp "game" als Key angegeben werden mit dem value "Monopoly".
	 * 
	 * Vielleicht �ndere ich das bei Zeit nochmal in einen eigenen Enumtyp um. ;)
	 */
	public void deleteAllEventsByRegex(Map<String, String> filter);

	/**
	 * Erstellt ein neues Event und tr�gt es in der Datenbank ein.
	 * 
	 * @param newEvent Das Event das hinzugef�gt werden soll.
	 */
	public void createEvent(EventData newEvent);

	/**
	 * Liefert ein Event passend zu einer ID.
	 * 
	 * @param id Die ID des gew�nschten Events.
	 */
	public Event findByID(String id);
	
//	public List<Event> findAllEventsByRegex(List<String> filter, Request req);
}

