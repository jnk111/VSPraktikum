package vs.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die Hauptkomponente für die Kommunikation mit den 
 * verschiedenen Services. 
 * 
 * Hier werden Anmeldevorgänge und Abfragen getätigt und von den Controllern
 * abgefragt.
 * 
 * @author Jones
 *
 */
public class RestopolyClient {

	/**
	 * TODO Dummydata für Informationen über laufende Spiele
	 * @return
	 */
	public List<GameInformation> getGameInformations() {
		// TODO Auto-generated method stub
		List<GameInformation> data = new ArrayList<>();
		data.add(new GameInformation("/game/idasd","Monopoly-Dummy-Data","4"));
		return data;
	}

}
