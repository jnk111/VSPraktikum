package vs.aufgabe2a.boardsservice.models.json;

/**
 * Place - JSON-Objekt -> {'name':'Los', 'broker': '/broker/places/{placeid}']}
 * RESTopoly-Spezifikation:  /boards/{gameid}/places/{place}
 * @author jan
 *
 */
public class JSONPlace implements JSONObject{
	
	private String name;			// Place-Name
	private String broker;		// URI to the Place at the Broker
	
	public JSONPlace(String name, String brokerURI){
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

	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}
}
