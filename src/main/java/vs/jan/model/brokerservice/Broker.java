package vs.jan.model.brokerservice;

import java.util.ArrayList;
import java.util.List;

import vs.jan.exception.ResourceNotFoundException;
import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.model.Convertable;
import vs.jan.model.exception.Error;

public class Broker implements Convertable<JSONBroker> {

	private String uri;
	private String gameUri;
	private String name;
	private String estateUri;
	private List<Place> places;

	public Broker() {
		this(null);
	}

	public Broker(String uri) {
		this(uri, null);
	}

	public Broker(String uri, String gameUri) {
		this(uri, gameUri, new ArrayList<>());
	}

	public Broker(String uri, String gameUri, List<Place> places) {

		this.uri = uri;
		this.gameUri = gameUri;
		this.places = places;

	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getGameUri() {
		return gameUri;
	}

	public void setGameUri(String gameUri) {
		this.gameUri = gameUri;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	@Override
	public JSONBroker convert() {
		
		JSONBroker broker = new JSONBroker();
		broker.setId(this.getUri());
		broker.setEstates(this.getEstateUri());
		broker.setGame(this.getGameUri());
		return broker;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estateUri == null) ? 0 : estateUri.hashCode());
		result = prime * result + ((gameUri == null) ? 0 : gameUri.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((places == null) ? 0 : places.hashCode());
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
		Broker other = (Broker) obj;
		if (estateUri == null) {
			if (other.estateUri != null)
				return false;
		} else if (!estateUri.equals(other.estateUri))
			return false;
		if (gameUri == null) {
			if (other.gameUri != null)
				return false;
		} else if (!gameUri.equals(other.gameUri))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (places == null) {
			if (other.places != null)
				return false;
		} else if (!places.equals(other.places))
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
		return "Broker [uri=" + uri + ", gameUri=" + gameUri + ", name=" + name + ", estateUri=" + estateUri + ", places="
				+ places + "]";
	}

	public void addPlace(Place p) {
		if(!this.places.contains(p)){
			this.places.add(p);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

	public String getEstateUri() {
		return estateUri;
	}

	public void setEstateUri(String estateUri) {
		this.estateUri = estateUri;
	}

	public Place getPlace(String placeid) {
		for(Place p: this.places){
			String [] u = p.getPlaceUri().split("/");
			if(u[u.length - 1].equals(placeid)){
				return p;
			}
		}
		throw new ResourceNotFoundException(Error.PLACE_NOT_FOUND.getMsg());
	}
	
	
}
