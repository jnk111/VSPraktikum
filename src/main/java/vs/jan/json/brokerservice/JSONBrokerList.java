package vs.jan.json.brokerservice;

import java.util.ArrayList;
import java.util.List;

public class JSONBrokerList {

	private List<String> broker;

	public JSONBrokerList() {
		setBroker(new ArrayList<>());
	}

	public List<String> getBroker() {
		return broker;
	}

	public void setBroker(List<String> broker) {
		this.broker = broker;
	}
	
}

