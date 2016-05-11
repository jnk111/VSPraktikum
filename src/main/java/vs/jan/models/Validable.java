package vs.jan.models;

public interface Validable {

	/**
	 * Prueft ob ein uebergebener JSON-String alle noetigen Informationen enthaelt
	 * 
	 * @return <code>true</code> : alle Informationen vorhanden
	 * 				 <code>false</code> : nicht alle Informationen vorhanden
	 */
	boolean isValid();
}
