package vs.jan.json.boardservice;

import java.util.Collection;

import vs.jonas.services.model.Event;

/**
 * Ein Wrapper fuer die Event-Collection.
 * 
 * Dieser ist noetig um sich gegen Crossidescripting Angriffe 
 * bei JSON-Strings in Verbindung mit Arrays abzusichern.
 * 
 * @author Jones
 *
 */
public class JSONEventList {

	private Collection<Event> events;
	
	public JSONEventList(Collection<Event> events){
		this.events = events;
	}
	
	public Collection<Event> getEvents(){
		return events;
	}
}
