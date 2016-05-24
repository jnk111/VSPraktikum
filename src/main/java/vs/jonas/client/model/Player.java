package vs.jonas.client.model;

import com.google.gson.Gson;
import vs.jonas.client.json.PlayerResponse;

public class Player {

	String name;
	String pawn;
	String account;
	String ready;
	
	public Player(String name, String pawn, String account, String ready) {
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
	
}
