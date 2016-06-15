package vs.jan.model.brokerservice;

import java.util.ArrayList;
import java.util.List;

import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.Convertable;
import vs.jan.model.Updatable;
import vs.jan.model.boardservice.Player;

public class Place implements Convertable<JSONPlace>, Updatable<JSONPlace> {
	
	private String uri;
	private String placeUri;
	private Player owner;
	private int price;
	private List<Integer> rent;
	private List<Integer> cost;
	private int houses;
	private String visitUri;
	private String hypoCreditUri;
	private boolean hypo;
	
	
	public Place(String uri, String placeUri, int rentPrice, int houses, String visitUri, String hypoCreditUri){
		
		this(uri, placeUri, null, rentPrice, new ArrayList<>(), new ArrayList<>(), houses, visitUri, hypoCreditUri);

	}
	
	public Place(String uri, String placeUri, Player owner, int price, List<Integer> rent, List<Integer> cost,
			int houses, String visitUri, String hypoCreditUri) {
		
		this.uri = uri;
		this.placeUri = placeUri;
		this.owner = owner;
		this.price = price;
		this.rent = rent;
		this.cost = cost;
		this.houses = houses;
		this.visitUri = visitUri;
		this.hypoCreditUri = hypoCreditUri;
		this.setHypo(false);
		initRents();
		initCosts();
	}

	public Place(Place place) {
		this.uri = place.getUri();
		this.placeUri = place.getPlaceUri();
		this.owner = place.getOwner();
		this.price = place.getPrice();
		this.rent = place.getRent();
		this.cost = place.getCost();
		this.houses = place.getHouses();
		this.visitUri = place.getVisitUri();
		this.hypoCreditUri = place.getHypoCreditUri();
		initRents();
		initCosts();
	}

	private void initCosts() {
		
		for(int i = 1; i < 6; i++){
			this.cost.add(this.price * (i * 10));
		}
	}

	private void initRents() {
		int price = this.price;
		this.rent.add(price);
		
		for(int i = 1; i < 6; i++){
			price = price * 2;
			this.rent.add(price);
		}
		
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPlaceUri() {
		return placeUri;
	}

	public void setPlaceUri(String placeUri) {
		this.placeUri = placeUri;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public List<Integer> getRent() {
		return rent;
	}

	public void setRent(List<Integer> rent) {
		this.rent = rent;
	}

	public List<Integer> getCost() {
		return cost;
	}

	public void setCost(List<Integer> cost) {
		this.cost = cost;
	}

	public int getHouses() {
		return houses;
	}

	public void setHouses(int houses) {
		this.houses = houses;
	}

	public String getVisitUri() {
		return visitUri;
	}

	public void setVisitUri(String visitUri) {
		this.visitUri = visitUri;
	}

	public String getHypoCreditUri() {
		return hypoCreditUri;
	}

	public void setHypoCreditUri(String hypoCreditUri) {
		this.hypoCreditUri = hypoCreditUri;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cost == null) ? 0 : cost.hashCode());
		result = prime * result + houses;
		result = prime * result + ((hypoCreditUri == null) ? 0 : hypoCreditUri.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((placeUri == null) ? 0 : placeUri.hashCode());
		result = prime * result + price;
		result = prime * result + ((rent == null) ? 0 : rent.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((visitUri == null) ? 0 : visitUri.hashCode());
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
		Place other = (Place) obj;
		if (cost == null) {
			if (other.cost != null)
				return false;
		} else if (!cost.equals(other.cost))
			return false;
		if (houses != other.houses)
			return false;
		if (hypoCreditUri == null) {
			if (other.hypoCreditUri != null)
				return false;
		} else if (!hypoCreditUri.equals(other.hypoCreditUri))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (placeUri == null) {
			if (other.placeUri != null)
				return false;
		} else if (!placeUri.equals(other.placeUri))
			return false;
		if (price != other.price)
			return false;
		if (rent == null) {
			if (other.rent != null)
				return false;
		} else if (!rent.equals(other.rent))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (visitUri == null) {
			if (other.visitUri != null)
				return false;
		} else if (!visitUri.equals(other.visitUri))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return "Place [uri=" + uri + ", placeUri=" + placeUri + ", owner=" + owner + ", price=" + price + ", rent=" + rent
				+ ", cost=" + cost + ", housesPrice=" + houses + ", visitUri=" + visitUri + ", hypoCreditUri="
				+ hypoCreditUri + "]";
	}

	@Override
	public JSONPlace convert() {
		
		JSONPlace place = new JSONPlace(this.placeUri);
		place.setCost(this.cost);
		place.setHouses(this.houses);
		place.setHypocredit(this.hypoCreditUri);
		place.setId(this.uri);
		
		if(this.owner != null){
			place.setOwner(this.owner.getId());
		}
		
		place.setPlace(this.placeUri);
		place.setRent(this.rent);
		place.setValue(this.price);
		place.setVisit(this.visitUri);
		
		return place;
	}


	@Override
	public void update(JSONPlace place) {
		
		if (place.getCost() != null) {
			this.setCost(place.getCost());
		}
		
		if(place.getHouses() > 0){
			this.setHouses(this.houses = place.getHouses());
		}
		
		if(place.getHypocredit() != null){
			this.setHypoCreditUri(place.getHypocredit());
		}
		
		if(place.getOwner() != null){
			// TODO: Owner
		}
		
		if(place.getValue() > 0){
			this.setPrice(place.getValue());
		}
		
		if(place.getRent() != null){
			this.setRent(place.getRent());
		}
		
		if(place.getVisit() != null){
			this.setVisitUri(place.getVisit());
		}
	}
	
	public boolean isHypo() {
		return hypo;
	}

	public void setHypo(boolean hypo) {
		this.hypo = hypo;
	}

	public boolean isPlace() {
		
		return this.price > 0;
	}
}
