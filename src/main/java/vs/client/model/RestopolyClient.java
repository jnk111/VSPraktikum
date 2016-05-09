package vs.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist die Hauptkomponente f�r die Kommunikation mit den 
 * verschiedenen Services. 
 * 
 * Hier werden Anmeldevorg�nge und Abfragen get�tigt und von den Controllern
 * abgefragt.
 * 
 * @author Jones
 *
 */
public class RestopolyClient {

	/**
	 * TODO Dummydata f�r Informationen �ber laufende Spiele
	 * @return
	 */
	public List<GameInformation> getGameInformations() {
		// TODO Auto-generated method stub
		List<GameInformation> data = new ArrayList<>();
		data.add(new GameInformation("/game/idasd","Monopoly-Dummy-Data","4"));
		return data;
	}

}
