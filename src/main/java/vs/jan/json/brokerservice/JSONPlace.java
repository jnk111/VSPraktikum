package vs.jan.json.brokerservice;

import java.util.List;

import vs.jan.model.Validable;

public class JSONPlace implements Validable{
	
	private String id;
	private String place;
	private String owner;
	private int value;
	private List<Integer> rent;
	private List<Integer> cost;
	private int houses;
	private String visit;
	private String hypocredit;
	

	public JSONPlace() {
		this(null);
	}

	public JSONPlace(String place){
		this(null, place, null, 0,  null, null, 0, null, null);
	}
	
	public JSONPlace(String id, String place, String owner, int value, List<Integer> rent, List<Integer> cost, int houses, String visit,
			String hypocredit) {
		
		this.id = id;
		this.place = place;
		this.owner = owner;
		this.value = value;
		this.rent = rent;
		this.cost = cost;
		this.houses = houses;
		this.visit = visit;
		this.hypocredit = hypocredit;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
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

	public String getVisit() {
		return visit;
	}

	public void setVisit(String visit) {
		this.visit = visit;
	}

	public String getHypocredit() {
		return hypocredit;
	}

	public void setHypocredit(String hypocredit) {
		this.hypocredit = hypocredit;
	}

	@Override
	public boolean isValid() {
		
		return this.place != null;
	}

	@Override
	public String toString() {
		return "JSONPlace [id=" + id + ", place=" + place + ", owner=" + owner + ", value=" + value + ", rent=" + rent
				+ ", cost=" + cost + ", houses=" + houses + ", visit=" + visit + ", hypocredit=" + hypocredit + "]";
	}
	
	
	
	
	
	
	
	

}
