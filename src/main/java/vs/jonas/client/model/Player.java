package vs.jonas.client.model;

import java.util.List;

import vs.jonas.client.json.Place;

public class Player {

	private String name;
	private String uri;
	private String account;
	private double averageDiceRoll;
	private List<Place> places;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public double getAverageDiceRoll() {
		return averageDiceRoll;
	}

	public void setAverageDiceRoll(double averageDiceRoll) {
		this.averageDiceRoll = averageDiceRoll;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}	
}
