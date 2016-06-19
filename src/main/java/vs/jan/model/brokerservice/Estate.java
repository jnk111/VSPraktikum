package vs.jan.model.brokerservice;

import java.util.ArrayList;
import java.util.List;

import vs.jan.exception.InvalidPlaceIDException;
import vs.jan.helper.brokerservice.BrokerHelper;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.Convertable;
import vs.jan.model.Updatable;
import vs.jan.model.brokerservice.PlaceColor;
import vs.jan.model.boardservice.Player;
import vs.jan.model.exception.Error;

public class Estate implements Convertable<JSONPlace>, Updatable<JSONPlace> {

	private final int MAX_HOUSES = 5;
	private final int MAX_STATIONS = 4;
	private final int MAX_FACTORY = 2;
	private final int COST_MULT = 2;

	private int position;
	private String uri;
	private String placeUri;
	private Player owner;
	private String ownerUri;
	private int price;
	private List<Integer> rent;
	private List<Integer> cost;
	private int houses;
	private String visitUri;
	private String hypoCreditUri;
	private boolean hypo;
	private PlaceColor color;

	public Estate(int num, String uri, String placeUri, int rentPrice, int houses, String visitUri, String hypoCreditUri,
			String owner) {

		this(num, uri, placeUri, null, rentPrice, new ArrayList<>(), new ArrayList<>(), houses, visitUri, hypoCreditUri,
				owner);

	}

	public Estate(int num, String uri, String placeUri, Player owner, int price, List<Integer> rent, List<Integer> cost,
			int houses, String visitUri, String hypoCreditUri, String ownerUri) {

		this.uri = uri;
		this.placeUri = placeUri;
		this.owner = owner;
		this.price = price;
		this.rent = rent;
		this.cost = cost;
		this.houses = houses;
		this.visitUri = visitUri;
		this.hypoCreditUri = hypoCreditUri;
		this.ownerUri = ownerUri;
		this.setHypo(false);
		this.position = num;
		initRents();
		initCosts();
		initColor();
	}

	public Estate(Estate place) {
		this.uri = place.getUri();
		this.placeUri = place.getPlaceUri();
		this.owner = place.getOwner();
		this.price = place.getPrice();
		this.rent = place.getRent();
		this.cost = place.getCost();
		this.houses = place.getHouses();
		this.visitUri = place.getVisitUri();
		this.hypoCreditUri = place.getHypoCreditUri();
		this.position = place.getPosition();
	}

	public void initInformation() {
		initRents();
		initCosts();
		initColor();
	}
	

	private void initCosts() {
		for (int i = 0; i < MAX_HOUSES; i++) {
			this.cost.add(this.price * ((i + 1) * COST_MULT));
		}
	}

	private void initRents() {
		int price = this.price;
		this.rent.add(price);

		for (int i = 0; i < MAX_HOUSES; i++) {
			price = price * 2;
			this.rent.add(price);
		}
	}

	private void initColor() throws InvalidPlaceIDException {

		String id = BrokerHelper.getID(this.placeUri);

		try {
			int num = Integer.parseInt(id);

			for (BoardPlace p : BoardPlace.values()) {
				if (p.ordinal() == num) {
					this.color = p.getColor();
					return;
				}
			}
		} catch (NumberFormatException e) {
			throw new InvalidPlaceIDException(Error.PLACE_ID_NUM.getMsg());
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
		Estate other = (Estate) obj;
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
				+ ", cost=" + cost + ", housesPrice=" + houses + ", visitUri=" + visitUri + ", hypoCreditUri=" + hypoCreditUri
				+ "]";
	}

	@Override
	public JSONPlace convert() {

		JSONPlace place = new JSONPlace(this.placeUri);
		place.setCost(this.cost);
		place.setHouses(this.houses);
		place.setHypocredit(this.hypoCreditUri);
		place.setId(this.uri);
		place.setOwner(this.ownerUri);
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

		if (place.getHouses() > 0) {
			this.setHouses(this.houses = place.getHouses());
		}

		if (place.getHypocredit() != null) {
			this.setHypoCreditUri(place.getHypocredit());
		}

		if (place.getValue() > 0) {
			this.setPrice(place.getValue());
		}

		if (place.getRent() != null) {
			this.setRent(place.getRent());
		}

		if (place.getVisit() != null) {
			this.setVisitUri(place.getVisit());
		}
	}

	public boolean isHypo() {
		return hypo;
	}

	public void setHypo(boolean hypo) {
		this.hypo = hypo;
	}

	public boolean isStreet() {

		return this.isBuyable() && this.color != null;
	}

	public String getOwnerUri() {
		return ownerUri;
	}

	public void setOwnerUri(String ownerUri) {
		this.ownerUri = ownerUri;
	}

	public PlaceColor getColor() {
		return this.color;
	}

	public void setColor(PlaceColor color) {
		this.color = color;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isCommunity() {
		return this.position == BoardPlace.Ereignis1.ordinal() || this.position == BoardPlace.Ereignis2.ordinal()
				|| this.position == BoardPlace.Ereignis3.ordinal();
	}

	public boolean isChance() {
		return this.position == BoardPlace.Ereignis1.ordinal() || this.position == BoardPlace.Ereignis2.ordinal()
				|| this.position == BoardPlace.Ereignis3.ordinal();
	}

	public boolean isTax() {
		return this.position == BoardPlace.EinkStr.ordinal() || this.position == BoardPlace.ZusatzStr.ordinal();
	}

	public boolean isFreeParking() {
		return this.position == BoardPlace.FreiParken.ordinal();
	}

	public boolean isJail() {
		return this.position == BoardPlace.InJail.ordinal() || this.position == BoardPlace.GoJail.ordinal();
	}

	public boolean isStation() {
		return this.position == BoardPlace.Bhf.ordinal() || this.position == BoardPlace.Suedbhf.ordinal()
				|| this.position == BoardPlace.Westbhf.ordinal() || this.position == BoardPlace.Nordbhf.ordinal();
	}
	
	public boolean isFactory() {
		return this.position == BoardPlace.EWerk.ordinal() || this.position == BoardPlace.Wasser.ordinal();
	}

	public boolean isBuyable() {
		return !(this.isJail() || this.isFreeParking() || this.isChance() || this.isCommunity() || this.isTax());
	}

}
