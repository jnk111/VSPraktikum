package vs.client.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import spark.utils.IOUtils;
import vs.aufgabe1.diceservice.Dice;

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

	/**
	 * Liefert das Würfelergebnis vom DiceService.
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
