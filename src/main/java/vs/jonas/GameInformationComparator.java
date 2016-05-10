package vs.jonas;

import java.util.Comparator;

/**
 * Diese Klasse implementiert einen einfachen Comparator,
 * um GameInformation s zu sortieren.
 * 
 * Aktuell sortiert sie nur nach der Anzahl Spieler.
 * 
 * @author Jones
 *
 */
public class GameInformationComparator implements Comparator<GameInformation> {

	@Override
	public int compare(GameInformation arg0, GameInformation arg1) {
		int result = 0;
		try{
			result = Integer.valueOf(arg0.getNumberOfPlayers()).compareTo(Integer.valueOf(arg1.getNumberOfPlayers()));
		}catch (Exception ex){
		}
		return result;
	}

}
