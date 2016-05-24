package vs.jan.model;

public class Player {
	private String id;
	private String user;
	private boolean ready;
	private String pawn;
	private String account;

	public Player(String user, String id, String pawn, String account, boolean ready) {
		this.user = user;
		this.id = id;
		this.pawn = pawn;
		this.account = account;
		this.ready = ready;
	}

	public Player() {
		this.ready = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return user;
	}

	public void setUserName(String user) {
		this.user = user;
	}

	public boolean getReadyness() {
		return ready;
	}

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

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public boolean isValid() {
		return getId() != null && getUserName() != null && getAccount() != null && getPawn() != null;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", user=" + user + ", pawn=" + pawn + ", account=" + account + "]";
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Player) {
			Player thatPlayer = (Player) that;

			return this.getUserName().equals(thatPlayer.getUserName()) 
							&& this.getId().equals(thatPlayer.getId());
		}
		return false;
	}

}
