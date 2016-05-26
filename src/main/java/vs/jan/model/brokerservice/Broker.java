package vs.jan.model.brokerservice;

import java.util.List;

import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.model.Convertable;

public class Broker implements Convertable<JSONBroker> {

	private String uri;
	private String gameUri;
	private List<Place> places;

	public Broker() {
		this(null);
	}

	public Broker(String uri) {
		this(uri, null);
	}

	public Broker(String uri, String gameUri) {
		this(uri, gameUri, null);
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
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gameUri == null) ? 0 : gameUri.hashCode());
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
		if (gameUri == null) {
			if (other.gameUri != null)
				return false;
		} else if (!gameUri.equals(other.gameUri))
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
		return "Broker [uri=" + uri + ", gameUri=" + gameUri + ", places=" + places + "]";
	}

}
