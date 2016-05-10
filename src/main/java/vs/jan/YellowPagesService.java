package vs.jan;

import java.util.HashMap;
import java.util.Map;

public class YellowPagesService {

	// ...
	
	private Map<String, Service> services;
	
	public YellowPagesService(){
		initServices();
	}

	private void initServices() {
		setServices(new HashMap<>());
		fetchAllServices();
	}

	
	
	
	private void fetchAllServices() {
		
	}

	public Service getEventService(){
		
		return null;
	}
	
	public Service getDiceService(){
		
		return null;
		
	}

	public Map<String, Service> getServices() {
		return services;
	}

	public void setServices(Map<String, Service> services) {
		this.services = services;
	}

	public Service getService(String key) {
		return this.services.get(key);
	}
	
}
