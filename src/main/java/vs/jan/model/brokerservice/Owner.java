package vs.jan.model.brokerservice;

import vs.jan.json.brokerservice.JSONOwner;
import vs.jan.model.Convertable;
import vs.jan.model.Updatable;

public class Owner implements Updatable<JSONOwner>, Convertable<JSONOwner>{

	private String uri;
	private String playerUri;
	private String pawnUri;
	private String accountUri;
	private String readyUri;
	
	
	public Owner(){
		
		this(null,null,null,null,null);
	}
	
	public Owner(String uri, String playerUri, String pawnUri, String accountUri, String readyUri) {
		super();
		this.uri = uri;
		this.playerUri = playerUri;
		this.pawnUri = pawnUri;
		this.accountUri = accountUri;
		this.readyUri = readyUri;
	}



	public String getUri() {
		return uri;
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

	public String getPawnUri() {
		return pawnUri;
	}

	public void setPawnUri(String pawnUri) {
		this.pawnUri = pawnUri;
	}

	public String getAccountUri() {
		return accountUri;
	}

	public void setAccountUri(String accountUri) {
		this.accountUri = accountUri;
	}

	public String getReadyUri() {
		return readyUri;
	}

	public void setReadyUri(String readyUri) {
		this.readyUri = readyUri;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountUri == null) ? 0 : accountUri.hashCode());
		result = prime * result + ((pawnUri == null) ? 0 : pawnUri.hashCode());
		result = prime * result + ((playerUri == null) ? 0 : playerUri.hashCode());
		result = prime * result + ((readyUri == null) ? 0 : readyUri.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Owner other = (Owner) obj;
		if (accountUri == null) {
			if (other.accountUri != null)
				return false;
		} else if (!accountUri.equals(other.accountUri))
			return false;
		if (pawnUri == null) {
			if (other.pawnUri != null)
				return false;
		} else if (!pawnUri.equals(other.pawnUri))
			return false;
		if (playerUri == null) {
			if (other.playerUri != null)
				return false;
		} else if (!playerUri.equals(other.playerUri))
			return false;
		if (readyUri == null) {
			if (other.readyUri != null)
				return false;
		} else if (!readyUri.equals(other.readyUri))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return "Owner [uri=" + uri + ", playerUri=" + playerUri + ", pawnUri=" + pawnUri + ", accountUri=" + accountUri
				+ ", readyUri=" + readyUri + "]";
	}

	@Override
	public void update(JSONOwner owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONOwner convert() {
		
		JSONOwner owner = new JSONOwner();
		owner.setAccount(this.getAccountUri());
		owner.setId(this.getUri());
		owner.setPawn(this.getPawnUri());
		owner.setReady(this.getReadyUri());
		owner.setUser(this.getPlayerUri());
		return owner;
	}

}
