package vs.jan.model;

public interface Convertable<T> {
	
	/**
	 * Konvertiert ein Convertable-Objekt in seine JSON-Repraesentation
	 * Diese kann dann mit z. B.: Gson in einen JSON-String geparst werden
	 * und als Response zurueckgegeben werden
	 * @return
	 * 				JSON-Repraesentation des Convertable-Objektes
	 */
	T convert();

}
