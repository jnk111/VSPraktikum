package vs.jonas.sandbox;
import java.util.List;

import com.google.gson.Gson;

public class Game {

	String id;
	String name;
	List<Player> players;
	
	
	public Game(String id, String name, List<Player> players) {
		super();
		this.id = id;
		this.name = name;
		this.players = players;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public List<Player> getPlayers() {
		return players;
	}
	
	public String toString(){
		return new Gson().toJson(this);
	}
}
