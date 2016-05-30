package vs.jonas.client.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Place {

	String id;
	String name;
	String owner;
	String value;
	String rent;
	String cost;
	String houses;
	String hypocredit;
	List<String> players;
	
	public Place(String id, String name, String owner, String value, String rent, String cost, String houses, String hypocredit) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.value = value;
		this.rent = rent;
		this.cost = cost;
		this.houses = houses;
		this.hypocredit = hypocredit;
		this.players = new ArrayList<>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setRent(String rent) {
		this.rent = rent;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public void setHouses(String houses) {
		this.houses = houses;
	}

	public void setHypocredit(String hypocredit) {
		this.hypocredit = hypocredit;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public String getValue() {
		return value;
	}

	public String getRent() {
		return rent;
	}

	public String getCost() {
		return cost;
	}

	public String getHouses() {
		return houses;
	}

	public String getHypocredit() {
		return hypocredit;
	}

	public String getID() {
		// TODO Auto-generated method stub
		return id;
	}
	
	public void setPlayers(List<String> players){
		this.players = players;
	}

	public List<String> getPlayers() {
		// TODO Auto-generated method stub
		return this.players;
	}

	public void setID(String id2) {
		this.id = id2;
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}
}
