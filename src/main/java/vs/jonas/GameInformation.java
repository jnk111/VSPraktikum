package vs.jonas;

/**
 * Diese Klasse liefert Informationen �ber Spiele.
 * Sie wird verwendet, um in der UI offene Spiele anzeigen zu k�nnen,
 * so dass der User ausw�hlen kann, welchem Spiel er beitreten m�chte.
 * 
 * Aktuell habe ich mir �berlegt, dass die Informationen wie ID, Name und 
 * die Anzahl an Spielern zu diesem Zweck ausreichen w�rden.
 * 
 * @author Jones
 *
 */
public class GameInformation{

	private String id; 
	private String name;
	private String numberOfPlayers;
	
	/**
	 * Initialisiert das Objekt
	 * @param id Die Uri des Games
	 * @param name Der Name des Spiels (z.B Monopoly)
	 * @param numberOfPlayers Die Anzahl der Spieler, die das Spiel spielen.
	 */
	public GameInformation(String id, String name, String numberOfPlayers) {
		super();
		this.id = id;
		this.name = name;
		this.numberOfPlayers = numberOfPlayers;
	}

	/**
	 * Liefert die URI des Spiels
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Liefert des Namen des Spiels
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Liefert die Anzahl der Spieler, die das Spiel spielen
	 * @return
	 */
	public String getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumberOfPlayers(String numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}
	
	
	
}
