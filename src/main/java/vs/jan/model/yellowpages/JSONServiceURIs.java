package vs.jan.model.yellowpages;

import java.util.ArrayList;
import java.util.List;

public class JSONServiceURIs {
	
	private List<String> services;
	
	public JSONServiceURIs() {
		this.services = new ArrayList<>();
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}
}
