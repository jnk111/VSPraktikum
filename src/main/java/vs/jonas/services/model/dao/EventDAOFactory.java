package vs.jonas.services.model.dao;

/**
 * Eine einfache Factory, die ermoeglicht, dass nur ein EventDAO existiert.
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
