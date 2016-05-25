package vs.jonas.sandbox;
import com.google.gson.Gson;

public class Player {
	
	String id;
	String name;
	
	
	
	public Player(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public String toString(){
		return new Gson().toJson(this);
	}
	
}
