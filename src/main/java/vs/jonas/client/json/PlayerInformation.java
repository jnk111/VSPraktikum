package vs.jonas.client.json;

import com.google.gson.Gson;

public class PlayerInformation {

	String name;
	String pawn;
	String account;
	String ready;
	
	public PlayerInformation(String name, String pawn, String account, String ready) {
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

	public String isReady() {
		return ready;
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}

	public String getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public String LastRoll() {
		// TODO Auto-generated method stub
		return null;
	}

	public String hasTurnMutex() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
