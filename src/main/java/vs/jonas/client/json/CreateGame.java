package vs.jonas.client.json;

import com.google.gson.Gson;

/**
 * Created by Jones on 24.05.2016.
 */
public class CreateGame {

    String name;

    public CreateGame(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
