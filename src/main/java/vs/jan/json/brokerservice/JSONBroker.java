package vs.jan.json.brokerservice;

import vs.jan.model.Validable;

public class JSONBroker implements Validable{

	private String id;
	private String name;
	private String estates;
	
	
	@Override
	public boolean isValid() {
		return true;
	}


	public String getEstates() {
		return estates;
	}


	public void setEstates(String estates) {
		this.estates = estates;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

}
