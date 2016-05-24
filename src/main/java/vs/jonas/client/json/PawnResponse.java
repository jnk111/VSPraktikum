package vs.jonas.client.json;

import com.google.gson.Gson;

/**
 * Created by Jones on 24.05.2016.
 */
public class PawnResponse {

    String id; // uri to ressource itself
    String player; // uri to player
    String place; // uri to the place
    String position; // uri to the position on the board
    String roll; // uri to the rolls of the player
    String move; // uri to the moves of the player

    public PawnResponse(String id, String player, String place, String position, String roll, String move) {
        this.id = id;
        this.player = player;
        this.place = place;
        this.position = position;
        this.roll = roll;
        this.move = move;
    }

    public String getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public String getPlace() {
        return place;
    }

    public String getPosition() {
        return position;
    }

    public String getRoll() {
        return roll;
    }

    public String getMove() {
        return move;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
