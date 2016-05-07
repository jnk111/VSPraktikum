package vs.aufgabe1b.models.responses;

import java.util.Collection;

import vs.aufgabe1b.models.Event;

/**
 * Ein Wrapper f�r die Event-Collection.
 * 
 * Dieser ist n�tig um sich gegen Crossidescripting Angriffe 
 * bei JSON-Strings in Verbindung mit Arrays abzusichern.
 * 
 * @author Jones
 *
 */
public class EventList {

	private Collection<Event> events;
	
	public EventList(Collection<Event> events){
		this.events = events;
	}
	
	public Collection<Event> getEvents(){
		return events;
	}
}
