package vs.jonas.client.json;

import com.google.gson.Gson;

/**
 * Created by Jones on 24.05.2016.
 */
public class PlayerResponse {

    String id;
    String user; // user URI
    String pawn; // pawn Uri
    String account; // account in the bank uri
    String ready; // uri to the ready status

    public PlayerResponse(String id, String user, String pawn, String account, String ready) {
        this.id = id;
        this.user = user;
        this.pawn = pawn;
        this.account = account;
        this.ready = ready;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getPawn() {
        return pawn;
    }

    public String getAccount() {
        return account;
    }

    public String getReady() {
        return ready;
    }
    

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		PlayerResponse other = (PlayerResponse) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String toString(){
        return new Gson().toJson(this);
    }
}
