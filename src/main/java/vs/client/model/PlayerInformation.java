package vs.client.model;

public class PlayerInformation {

	String name;
	String pawn;
	String account;
	boolean ready;
	
	public PlayerInformation(String name, String pawn, String account, boolean ready) {
		super();
		this.name = name;
		this.pawn = pawn;
		this.account = account;
		this.ready = ready;
	}

	public String getName() {
		return name;
	}

	public String getPawn() {
		return pawn;
	}

	public String getAccount() {
		return account;
	}

	public boolean isReady() {
		return ready;
	}
	
	
	
}
