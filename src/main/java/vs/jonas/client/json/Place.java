package vs.jonas.client.json;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import com.google.gson.Gson;

public class Place {

	String id;
	String name;
	String broker; // uri to broker
	String owner;
	int value;
	int rent;
	int cost;
	int houses;
	String hypocredit;
	List<String> players;
	
	public Place(String id, String name, String broker, String owner, int value, int rent, int cost, int houses, String hypocredit) {
		super();
		this.id = id;
		this.name = name;
		this.broker = broker;
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

	public void setHouses(int houses) {
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

	public int getHouses() {
		return houses;
	}

	public String getHypocredit() {
		return hypocredit;
	}

	public String getID() {
		return id;
	}
	
	public void setPlayers(List<String> players){
		this.players = players;
	}

	public List<String> getPlayers() {
		return this.players;
	}

	public void setID(String id2) {
		this.id = id2;
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}
	
	

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getRent() {
		return rent;
	}

	public void setRent(int rent) {
		this.rent = rent;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public JComboBox<String> getPlayersASComboBox() {
		// TODO Auto-generated method stub
		JComboBox<String> box = new JComboBox<>();
		for(String player : this.players){
			box.addItem(player);
		}
		return box;
	}
}
