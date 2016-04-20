package vs.aufgabe1b.interfaces;

import java.util.List;
import java.util.Map;

import vs.aufgabe1b.models.Event;

public interface EventDAO {

	/**
	 * Liefert alle vorhandenen Events.
	 */
	public List<Event> getEvents();
	
	/**
	 * Liefert alle vorhandenen Events anhand von regulären Ausdrücken.
	 * 
	 * @param filter Enthält die geforderten Filter.
	 * 
	 * Beispiel: Werden zum Beispiel Events vom Spiel "Monopoly" gesucht,
	 * so muss in der Map der Filtertyp "game" als Key angegeben werden mit dem value "Monopoly".
	 * 
	 * Vielleicht ändere ich das bei Zeit nochmal in einen eigenen Enumtyp um. ;)
	 */
	public List<Event> findAllEventsByRegex(Map<String,String> filter);

	/**
	
	 * Löscht alle Events, die dem regulären Ausdruck entsprechen.
	 * 
	 * @param filter Enthält die geforderten Filter.
	 * 
	 * Beispiel: Werden zum Beispiel Events vom Spiel "Monopoly" gesucht,
	 * so muss in der Map der Filtertyp "game" als Key angegeben werden mit dem value "Monopoly".
	 * 
	 * Vielleicht ändere ich das bei Zeit nochmal in einen eigenen Enumtyp um. ;)
	 */
	public void deleteAllEventsByRegex(Map<String, String> filter);

	/**
	 * Erstellt ein neues Event und trägt es in der Datenbank ein.
	 * 
	 * @param newEvent Das Event das hinzugefügt werden soll.
	 */
	public void createEvent(Event newEvent);

	/**
	 * Liefert ein Event passend zu einer ID.
	 * 
	 * @param id Die ID des gewünschten Events.
	 */
	public Event findByID(String id);
	
//	public List<Event> findAllEventsByRegex(List<String> filter, Request req);
}

