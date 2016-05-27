package vs.jan.json.brokerservice;

import vs.jan.model.Validable;

public class JSONBroker implements Validable{

	private String id;
	private String name;
	private String estates;
	private String game;
	
	
	
	public JSONBroker(){
		this(null, null, null, null);
	}

	public JSONBroker(String id, String name, String estates, String game) {
		super();
		this.id = id;
		this.name = name;
		this.estates = estates;
		this.game = game;
	}


	public String getEstates() {
		return estates;
	}


	public void setEstates(String estates) {
		this.estates = estates;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	public void setGame(String game) {
		this.game = game;
		
	}


	public String getGame() {
		return game;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estates == null) ? 0 : estates.hashCode());
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		JSONBroker other = (JSONBroker) obj;
		if (estates == null) {
			if (other.estates != null)
				return false;
		} else if (!estates.equals(other.estates))
			return false;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return "JSONBroker [id=" + id + ", name=" + name + ", estates=" + estates + ", game=" + game + "]";
	}

	@Override
	public boolean isValid() {
		return this.name != null && this.game != null;
	}



	
	

}
