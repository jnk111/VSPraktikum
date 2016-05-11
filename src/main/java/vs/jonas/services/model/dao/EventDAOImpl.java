package vs.jonas.services.model.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import vs.jonas.services.json.EventData;
import vs.jonas.services.model.Event;

public class EventDAOImpl implements EventDAO {

	// Die Datenbank...
	private List<Event> events;
	private int nextId;
	
	/**
	 * Initialisiert das EventDAO. 
	 * 
	 * Normalerweise ist ein DAO (Data-Access-Object) ein Bindeglied zur Datenbank.
	 * In diesem Fall enthaelt dieses Objekt die Datenbank selbst,
	 * kann aber jederzeit um eine richtige Datenbank erweitert werden.
	 */
	public EventDAOImpl() {
		events = Collections.synchronizedList(new ArrayList<>());
		nextId = 0;
	}

	/**
	 * Liefert alle vorhandenen Events.
	 */
	@Override
	public synchronized List<Event> getEvents() {
		return events;
	}

	/**
	 * Liefert alle vorhandenen Events anhand von regul�ren Ausdr�cken.
	 * 
	 * @param filter Enthaelt die geforderten Filter.
	 * 
	 * Beispiel: Werden zum Beispiel Events vom Spiel "Monopoly" gesucht,
	 * so muss in der Map der Filtertyp "game" als Key angegeben werden mit dem value "Monopoly".
	 * 
	 * Vielleicht aendere ich das bei Zeit nochmal in einen eigenen Enumtyp um. ;)
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
	 * loescht alle Events, die dem regul�ren Ausdruck entsprechen.
	 * 
	 * @param filter Enthaelt die geforderten Filter.
	 * 
	 * Beispiel: Werden zum Beispiel Events vom Spiel "Monopoly" gesucht,
	 * so muss in der Map der Filtertyp "game" als Key angegeben werden mit dem value "Monopoly".
	 * 
	 * Vielleicht aendere ich das bei Zeit nochmal in einen eigenen Enumtyp um. ;)
	 */
	@Override
	public synchronized void deleteAllEventsByRegex(Map<String, String> filter) {
		List<Event> events = findAllEventsByRegex(filter);
		for(Event event : events){
			this.events.remove(event);
		}
	}

	/**
	 * Erstellt ein neues Event und traegt es in der Datenbank ein.
	 * 
	 * @param newEvent Das Event das hinzugefuegt werden soll.
	 */
	@Override
	public synchronized void createEvent(EventData newEvent) {
		System.out.println("Create new Event: " + newEvent);
		
		String game = newEvent.getGame();
		String type = newEvent.getType();
		String name = newEvent.getName();
		String reason = newEvent.getReason();
		String ressource = newEvent.getRessource();
		String player = newEvent.getPlayer();
		
		Event event = new Event(game,type,name,reason,ressource,player);
		
		events.add(event);
		System.out.println(events);
	}


	/**
	 * Liefert ein Event passend zu einer ID.
	 * 
	 * @param id Die ID des gewuenschten Events.
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
	 * Pr�ft ob ein Event alle Filterbedingungen erf�llt.
	 * 
	 * @param event Das zu �berpr�fende Event.
	 * @param filter Die Filter die gepr�ft werden sollen.
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
	
	public synchronized void incrementID(){
		this.nextId = this.nextId + 1;
	}
}
