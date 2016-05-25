package vs.jonas.client.json;

public class User {

	private String uri;
	private String user;

	public User(String name) {
		this.user = name;
		this.uri = null;
	}
	
	public String getName() {
		return user;
	}

	public String getUri() {
		return uri;
	}

	public void setName(String name) {
		this.user = name;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
}
