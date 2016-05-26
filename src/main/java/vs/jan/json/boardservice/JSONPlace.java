package vs.jan.json.boardservice;

/**
 * Place - JSON-Objekt -> {'name':'Los', 'broker': '/broker/places/{placeid}']}
 * RESTopoly-Spezifikation: /boards/{gameid}/places/{place}
 * 
 * @author jan
 *
 */
public class JSONPlace {

	private String name; // Place-Name
	private String broker; // URI to the Place at the Broker

	public JSONPlace(String name, String brokerURI) {
		this.name = name;
		this.broker = brokerURI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	/**
	 * TODO: implementieren, wenn Broker implementiert wird Prueft ob der
	 * uebergebene Place gueltig ist Keines der Felder darf null sein. Falls nicht
	 * alle benoetigt werden, sollte der entsprechende Konstruktor aufgerufen
	 * werden.
	 * 
	 * @return <code>true</code> : gueltig, <code>false</code> unguelitg
	 */
	public boolean isValid() {

		return true;
	}
}
