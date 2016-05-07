package vs.aufgabe2a.boardsservice.models.json;

/**
 * Dummy interface um Convertable implementieren zu koennen
 * Nicht als Statischen Typ benutzen -> keine Operationen
 * @author jan
 *
 */
public interface JSONObject {
	
	/**
	 * Prueft ob die uebergebenen Daten im Requestbody gueltig sind bzw.
	 * ob die Schnittstelle eingehalten wurde
	 * @return 
	 * 				<code>true</code> : gueltig, <code>false</code> : ungueltig
	 */
	boolean isValid();

}
