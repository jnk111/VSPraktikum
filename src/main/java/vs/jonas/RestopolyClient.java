package vs.jonas;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import spark.utils.IOUtils;

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

	public List<PlayerInformation> getPlayerInformations() {
		// TODO Auto-generated method stub
		List<PlayerInformation> data = new ArrayList<>();
		data.add(new PlayerInformation("dummy", "pawn", "2000", true));
		return data;
	}

	/**
	 * Liefert das W�rfelergebnis vom DiceService.
	 * Der DiceService muss laufen, damit ein Ergebnis errechnet werden kann.
	 * @return Das Wurfergebnis
	 * @throws IOException
	 */
	public int rollDice() throws IOException {
		URL url = new URL("http://localhost:4567/dice");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.connect(); //do it
		
		//Get response
		int code = connection.getResponseCode();
		
		String resBody = code < 400 ? IOUtils.toString(connection.getInputStream()) 
				: IOUtils.toString(connection.getErrorStream());
		Dice dice = new Gson().fromJson(resBody, Dice.class);
		
		return dice.getNumber();
	}


}
