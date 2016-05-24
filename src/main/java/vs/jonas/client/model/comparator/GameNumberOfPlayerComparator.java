package vs.jonas.client.model.comparator;

import java.util.Comparator;

import vs.jonas.client.json.GameResponse;

/**
 * Diese Klasse implementiert einen einfachen Comparator,
 * um GameResponse s zu sortieren.
 * 
 * Aktuell sortiert sie nur nach der Anzahl Spieler.
 * 
 * @author Jones
 *
 */
public class GameNumberOfPlayerComparator implements Comparator<GameResponse> {

	@Override
	public int compare(GameResponse arg0, GameResponse arg1) {
		int result = 0;
		try{
			result = Integer.valueOf(arg0.getNumberOfPlayers()).compareTo(Integer.valueOf(arg1.getNumberOfPlayers()));
		}catch (Exception ex){
		}
		return result;
	}

}
