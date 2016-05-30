package vs.jonas.client.json;

import com.google.gson.Gson;

public class PlayerInformation {

	String pawn;
	String account;
	String position;
	String lastRoll;
	boolean isReady;
	boolean hasTurn;

	public String getPawn() {
		return pawn;
	}

	public void setPawn(String pawn) {
		this.pawn = pawn;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getLastRoll() {
		return lastRoll;
	}

	public void setLastRoll(String lastRoll) {
		this.lastRoll = lastRoll;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public boolean hasTurnMutex() {
		return hasTurn;
	}

	public void setHasTurn(boolean hasTurn) {
		this.hasTurn = hasTurn;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
