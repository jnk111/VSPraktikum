package vs.jan.json.brokerservice;

import vs.jan.model.Validable;

public class JSONOwner implements Validable{

	private String id;
	private String user;
	private String pawn;
	private String account;
	private String ready;
	
	
	public JSONOwner(){
		this(null, null, null, null, null);
	}
	
	public JSONOwner(String id, String user, String pawn, String account, String ready) {
		super();
		this.id = id;
		this.user = user;
		this.pawn = pawn;
		this.account = account;
		this.ready = ready;
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

	public String getReady() {
		return ready;
	}

	public void setReady(String ready) {
		this.ready = ready;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((pawn == null) ? 0 : pawn.hashCode());
		result = prime * result + ((ready == null) ? 0 : ready.hashCode());
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
		JSONOwner other = (JSONOwner) obj;
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
		if (ready == null) {
			if (other.ready != null)
				return false;
		} else if (!ready.equals(other.ready))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "JSONOwner [id=" + id + ", user=" + user + ", pawn=" + pawn + ", account=" + account + ", ready=" + ready
				+ "]";
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

}
