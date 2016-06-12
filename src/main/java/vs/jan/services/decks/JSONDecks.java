package vs.jan.services.decks;

import vs.jan.model.Validable;

public class JSONDecks implements Validable{

	private String id;
	private String chance;
	private String community;
	
	public String getUri() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chance == null) ? 0 : chance.hashCode());
		result = prime * result + ((community == null) ? 0 : community.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		JSONDecks other = (JSONDecks) obj;
		if (chance == null) {
			if (other.chance != null)
				return false;
		} else if (!chance.equals(other.chance))
			return false;
		if (community == null) {
			if (other.community != null)
				return false;
		} else if (!community.equals(other.community))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JSONDecks [id=" + id + ", chance=" + chance + ", community=" + community + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChance() {
		return chance;
	}

	public void setChance(String chance) {
		this.chance = chance;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Override
	public boolean isValid() {
		
		return this.id != null && this.chance != null && this.community != null;
	}
	
	

}
