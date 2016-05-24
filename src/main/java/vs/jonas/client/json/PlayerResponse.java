package vs.jonas.client.json;

import com.google.gson.Gson;

/**
 * Created by Jones on 24.05.2016.
 */
public class PlayerResponse {

    String id;
    String user; // user URI
    String pawn; // pawn Uri
    String account; // account in the bank uri
    String ready; // uri to the ready status

    public PlayerResponse(String id, String user, String pawn, String account, String ready) {
        this.id = id;
        this.user = user;
        this.pawn = pawn;
        this.account = account;
        this.ready = ready;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getPawn() {
        return pawn;
    }

    public String getAccount() {
        return account;
    }

    public String getReady() {
        return ready;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
