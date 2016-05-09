package vs.aufgabe1b.models.daos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import vs.aufgabe1b.interfaces.EventDAO;
import vs.aufgabe1b.models.Event;

public class EventDAOImpl implements EventDAO {

	List<Event> events;
	
	/**
	 * Initialisiert das EventDAO. 
	 * 
	 * Normalerweise ist ein DAO (Data-Access-Object) ein Bindeglied zur Datenbank.
	 * In diesem Fall enthält dieses Objekt die Datenbank selbst,
	 * kann aber jederzeit um eine richtige Datenbank erweitert werden.
	 */
	public EventDAOImpl() {
		events = Collections.synchronizedList(new ArrayList<>());
		
		// Testdaten erstellen
		Event event = new Event("Monopoly","type1","event1", "reason1" , "ressource1", "player1");
//		Event event2 = new Event("Monopoly","type2","event2", "reason2" , "ressource2", "player2");
//		Event event3 = new Event("Monopoly","type3","event3", "reason3" , "ressource3", "player3");
//		Event event4 = new Event("Monopoly","type4","event4", "reason4" , "ressource4", "player4");
//		Event event5 = new Event("Monopoly","type5","event5", "reason5" , "ressource5", "player5");
		events.add(event);
//		events.add(event2);
//		events.add(event3);
//		events.add(event4);
//		events.add(event5);
	}

	/**
	 * Liefert alle vorhandenen Events.
	 */
	@Override
	public synchronized List<Event> getEvents() {
		return events;
	}

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
	@Override
	public synchronized List<Event> findAllEventsByRegex(Map<String, String> filter) {
		List<Event> choosenEvents = new ArrayList<>();
		
		for(Event event : events){
			if(matchesFilter(event,filter)){
				choosenEvents.add(event);
			}
		}
		return choosenEvents;
	}

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
	@Override
	public synchronized void deleteAllEventsByRegex(Map<String, String> filter) {
		List<Event> events = findAllEventsByRegex(filter);
		for(Event event : events){
			this.events.remove(event);
		}
	}

	/**
	 * Erstellt ein neues Event und trägt es in der Datenbank ein.
	 * 
	 * @param newEvent Das Event das hinzugefügt werden soll.
	 */
	@Override
	public synchronized void createEvent(Event newEvent) {
		System.out.println("Create new Event: " + newEvent);
		events.add(newEvent);
		System.out.println(events);
	}


	/**
	 * Liefert ein Event passend zu einer ID.
	 * 
	 * @param id Die ID des gewünschten Events.
	 */
	@Override
	public synchronized Event findByID(String id) {
		System.out.println("Eventid: " + id);
		for(Event event : events){
			if(event.getId().equals("events/"+id)){
				return event;
			}
		}
		return null;
	}

	/**
	 * Prüft ob ein Event alle Filterbedingungen erfüllt.
	 * 
	 * @param event Das zu überprüfende Event.
	 * @param filter Die Filter die geprüft werden sollen.
	 */
	private boolean matchesFilter(Event event, Map<String, String> filter) {
		boolean matchesAll = true;
		
		for(String s : filter.keySet()){
			switch (s) {
			case "game": {
				if(!filter.get(s).matches(event.getGame())){
					matchesAll = false;
				}
				break;
			}
			case "type": {
				if(!filter.get(s).matches(event.getType())){
					matchesAll = false;
				}
				break;
			}
			case "name": {
				if (!filter.get(s).matches(event.getName())) {
					matchesAll = false;
				}
				break;
			}
			case "reason": {
				if (!filter.get(s).matches(event.getReason())) {
					matchesAll = false;
				}
				break;
			}
			case "ressource": {
				if (!filter.get(s).matches(event.getRessource())) {
					matchesAll = false;
				};
				break;
			}
			case "player": {
				if (!filter.get(s).matches(event.getPlayer())) {
					matchesAll = false;
					break;
				}
			}
			}
		}
		return matchesAll;
	}
}
