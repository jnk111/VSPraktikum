package vs.jan.model.boardservice;

import vs.jan.model.Validable;

public class Player implements Validable{
	
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	@Override
	public String toString() {
		return "Player [id=" + id + ", user=" + user + ", pawn=" + pawn + ", account=" + account + "]";
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((pawn == null) ? 0 : pawn.hashCode());
		result = prime * result + (ready ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (pawn == null) {
			if (other.pawn != null)
				return false;
		} else if (!pawn.equals(other.pawn))
			return false;
		if (ready != other.ready)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	@Override
	public boolean isValid() {
		
		// Temp -> TODO: check account
		return getId() != null && getUser() != null && getPawn() != null;
	}

}
