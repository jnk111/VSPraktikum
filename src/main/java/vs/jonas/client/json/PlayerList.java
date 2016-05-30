package vs.jonas.client.json;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Jones on 24.05.2016.
 */
public class PlayerList {

    List<PlayerID> players;

    public PlayerList(List<PlayerID> players) {
        this.players = players;
    }

    public List<PlayerID> getPlayers() {
        return players;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
