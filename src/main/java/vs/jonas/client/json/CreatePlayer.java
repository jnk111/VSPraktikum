package vs.jonas.client.json;

public class CreatePlayer {

	String user;
	String ready;
	
	public CreatePlayer(String user, String ready){
		this.user = user;
		this.ready = ready;
	}

	public String getUser() {
		return user;
	}

	public String getReady() {
		return ready;
	}
	
	
}
