package vs.jan.json.brokerservice;

import vs.jan.model.Validable;

public class JSONPlace implements Validable{
	
	private String id;
	private String place;
	private String owner;
	private int value;
	private int [] rent;
	private int [] cost;
	private int houses;
	private String visit;
	private String hypocredit;
	
	public JSONPlace(String id, String place, String owner, int value, int[] rent, int[] cost, int houses, String visit,
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

	public int[] getRent() {
		return rent;
	}

	public void setRent(int[] rent) {
		this.rent = rent;
	}

	public int[] getCost() {
		return cost;
	}

	public void setCost(int[] cost) {
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
		
		return this.id != null && this.place != null;
	}
	
	
	
	

}
