package vs.aufgabe2a.boardsservice.models;

import vs.aufgabe2a.boardsservice.models.json.JSONObject;

public interface Convertable {
	
	/**
	 * Konvertiert ein Convertable-Objekt in seine JSON-Repraesentation
	 * Diese kann dann mit z. B.: Gson in einen JSON-String geparst werden
	 * und als Response zurueckgegeben werden
	 * @return
	 * 				JSON-Repraesentation des Convertable-Objektes
	 */
	JSONObject convert();

}
