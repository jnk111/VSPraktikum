package vs.jonas.client.json;

public class User {

	private String uri;
	private String user;
	private String playerUri; // nicht Raml-konform.. wird aber dennoch ben�tigt. ;)

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

	public String getPlayerUri() {
		return playerUri;
	}

	public void setPlayerUri(String playerUri) {
		this.playerUri = playerUri;
	}

	@Override
	public String toString() {
		return "User [uri=" + uri + ", user=" + user + ", playerUri=" + playerUri + "]";
	}
	
	
}
