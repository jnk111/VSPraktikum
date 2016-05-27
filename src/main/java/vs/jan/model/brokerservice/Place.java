package vs.jan.model.brokerservice;

import java.util.List;

import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.Convertable;

public class Place implements Convertable<JSONPlace> {
	
	private String uri;
	private String placeUri;
	private String ownerUri;
	private int price;
	private List<Integer> rent;
	private List<Integer> cost;
	private int housesPrice;
	private String visitUri;
	private String hypoCreditUri;
	
	
	public Place(){
		this(null, null, null,-1,null,null,-1,null,null);
	}
	
	public Place(String uri, String placeUri){
		
		this(uri, placeUri, null, -1, null, null, -1, null, null);
	}
	
	public Place(String uri, String placeUri, String ownerUri, int price, List<Integer> rent, List<Integer> cost,
			int housesPrice, String visitUri, String hypoCreditUri) {
		
		this.uri = uri;
		this.placeUri = placeUri;
		this.ownerUri = ownerUri;
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

	public String getOwnerUri() {
		return ownerUri;
	}

	public void setOwnerUri(String ownerUri) {
		this.ownerUri = ownerUri;
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
		result = prime * result + ((ownerUri == null) ? 0 : ownerUri.hashCode());
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
		if (ownerUri == null) {
			if (other.ownerUri != null)
				return false;
		} else if (!ownerUri.equals(other.ownerUri))
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
		return "Place [uri=" + uri + ", placeUri=" + placeUri + ", ownerUri=" + ownerUri + ", price=" + price + ", rent="
				+ rent + ", cost=" + cost + ", housesPrice=" + housesPrice + ", visitUri=" + visitUri + ", hypoCreditUri="
				+ hypoCreditUri + "]";
	}

	@Override
	public JSONPlace convert() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	

}
