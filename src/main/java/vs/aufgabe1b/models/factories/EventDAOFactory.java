package vs.aufgabe1b.models.factories;

import vs.aufgabe1b.interfaces.EventDAO;
import vs.aufgabe1b.models.daos.EventDAOImpl;

/**
 * Eine einfache Factory, die ermöglicht, dass nur ein EventDAO existiert.
 * @author Jones
 *
 */
public class EventDAOFactory {

	private static EventDAO dao;
	
	/**
	 * Liefert das Data-Access-Object von Events.
	 * @return
	 */
	public static EventDAO getDAO(){
		if(dao != null){
			return dao;
		}
		return dao = new EventDAOImpl();
	}
}
