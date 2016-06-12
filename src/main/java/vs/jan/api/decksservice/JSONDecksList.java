package vs.jan.api.decksservice;

import java.util.ArrayList;
import java.util.List;

public class JSONDecksList {
	
	private List<String> decks;
	
	public JSONDecksList(){
		this.decks = new ArrayList<>();
	}

	public List<String> getDecks() {
		return decks;
	}

	public void setDecks(List<String> decks) {
		this.decks = decks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((decks == null) ? 0 : decks.hashCode());
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
		JSONDecksList other = (JSONDecksList) obj;
		if (decks == null) {
			if (other.decks != null)
				return false;
		} else if (!decks.equals(other.decks))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JSONDecksList [decks=" + decks + "]";
	}
	
	public void addUri(String uri) {
		this.decks.add(uri);
	}
}
