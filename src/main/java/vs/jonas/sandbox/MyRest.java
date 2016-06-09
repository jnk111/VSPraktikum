package vs.jonas.sandbox;
import static spark.Spark.get;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.json.User;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.services.services.YellowPagesService;

/**
 * Created by Jones on 07.04.2016.
 */
public class MyRest {

    static String jsonMarker = "application/json";

    public static void main(String args[]){
        get("/hello", (req, res) -> {
            return "hello";
        });
        
        get("/games" , (req, res) -> {
        	GameList games = new GameList();
        	List<Player> players = new ArrayList<>();
        	Player p1 = new Player("/players/1", "joe");
        	Player p2 = new Player("/players/2", "Max");
        	
        	players.add(p1);
        	players.add(p2);
        	
        	games.add(new Game("/games/1", "Monopoly", players));
        	
        	return new Gson().toJson(games);
        });

        try {
        	YellowPagesService yellowPages = new YellowPagesService(false);
			RestopolyClient client = new RestopolyClient(yellowPages);
			
			JsonObject response = client.get("http://localhost:4567/games");
			System.out.println(response);
			
			JsonArray games = response.getAsJsonArray("games");
			
			for (int i = 0; i < games.size(); i++) {
				Game game = new Gson().fromJson(games.get(i),Game.class);
				System.out.println(game);
				
			}
			
			
		} catch (IOException | UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}
