package vs.jonas.sandbox;
import java.util.ArrayList;
import java.util.List;

public class GameList {

	List<Game> games;
	
	public GameList(){
		games = new ArrayList<>();
	}
	
	public void add(Game game){
		games.add(game);
	}
	
	public List<Game> getGames(){
		return games;
	}
}
