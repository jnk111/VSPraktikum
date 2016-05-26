package vs.jan.model;

public class Service{
	
	private String relUri;
	private String desciption;
	private String name;
	private String service;
	private String status;
	private String fullUri;
	
	public Service(){
		
		this(null, null, null, null, null, null);
		
	}
	
	public Service(String relUri, String desciption, 
								 String name, String service, 
								 String status, String fullUri) {
		
		this.relUri = relUri;
		this.desciption = desciption;
		this.name = name;
		this.service = service;
		this.status = status;
		this.fullUri = fullUri;
	}

	public String getRelUri() {
		return relUri;
	}

	public void setRelUri(String relUri) {
		this.relUri = relUri;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFullUri() {
		return fullUri;
	}

	public void setFullUri(String fullUri) {
		this.fullUri = fullUri;
	}

	@Override
	public int hashCode() {
		
		return (this.getName().hashCode() 
						+ this.getFullUri().hashCode() 
							+ this.getService().hashCode()) * 2; 
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(obj == this){
			return true;
		}
		
		if(obj instanceof Service){
			Service s = (Service) obj;
			
			return this.getName().equals(s.getName())
							&& this.getService().equals(s.getService())
								&& this.getFullUri().equals(s.getFullUri());
		}
		
		return false;
	}

	@Override
	public String toString() {
		
		return "service: " + this.getService() 
						+ ", full uri: " + this.getFullUri() 
							+ ", name " + this.getName() 
								+", status: " + this.getStatus();
	}
	
	
}


