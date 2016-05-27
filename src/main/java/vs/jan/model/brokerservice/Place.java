package vs.jan.model.brokerservice;

import java.util.ArrayList;
import java.util.List;

import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.Convertable;
import vs.jan.model.Updatable;

public class Place implements Convertable<JSONPlace>, Updatable<JSONPlace> {
	
	private String uri;
	private String placeUri;
	private Owner owner;
	private int price;
	private List<Integer> rent;
	private List<Integer> cost;
	private int housesPrice;
	private String visitUri;
	private String hypoCreditUri;
	
	
	public Place(String placeUri){
		this(null, placeUri, null,-1, new ArrayList<>(), new ArrayList<>(),-1,null,null);
	}
	
	public Place(String uri, String placeUri){
		
		this(uri, placeUri, null, -1, new ArrayList<>(), new ArrayList<>(), -1, null, null);
	}
	
	public Place(String uri, String placeUri, Owner owner, int price, List<Integer> rent, List<Integer> cost,
			int housesPrice, String visitUri, String hypoCreditUri) {
		
		this.uri = uri;
		this.placeUri = placeUri;
		this.owner = owner;
		this.price = price;
		this.rent = rent;
		this.cost = cost;
		this.housesPrice = housesPrice;
		this.visitUri = visitUri;
		this.hypoCreditUri = hypoCreditUri;
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

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
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

	public int getHousesPrice() {
		return housesPrice;
	}

	public void setHousesPrice(int housesPrice) {
		this.housesPrice = housesPrice;
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
		result = prime * result + housesPrice;
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
		if (housesPrice != other.housesPrice)
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
				+ ", cost=" + cost + ", housesPrice=" + housesPrice + ", visitUri=" + visitUri + ", hypoCreditUri="
				+ hypoCreditUri + "]";
	}

	@Override
	public JSONPlace convert() {
		
		JSONPlace place = new JSONPlace(this.getPlaceUri());
		place.setCost(this.getCost());
		place.setHouses(this.getHousesPrice());
		place.setHypocredit(this.getHypoCreditUri());
		place.setId(this.getUri());
		place.setOwner(this.owner.getUri());
		place.setPlace(this.getPlaceUri());
		place.setRent(this.getRent());
		place.setValue(this.getPrice());
		place.setVisit(this.getVisitUri());
		
		return place;
	}


	@Override
	public void update(JSONPlace place) {
		
		if (place.getCost() != null) {
			this.setCost(place.getCost());
		}
		
		if(place.getHouses() > 0){
			this.setHousesPrice(0);
		}
		
		if(place.getHypocredit() != null){
			this.setHypoCreditUri(place.getHypocredit());
		}
		
		if(place.getOwner() != null){
			// TODO: get Owner
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
}
