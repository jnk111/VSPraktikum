package vs.jonas.client.json;

/**
 * Created by Jones on 24.05.2016.
 */
public class GameResponse {

    String id;
    String name;
    String players; // uri
    ServiceList services;
    Components components;
    int numberOfPlayers;

    public GameResponse(String id, String name, String players, ServiceList services, Components components) {
        this.id = id;
        this.name = name;
        this.players = players;
        this.services = services;
        this.components = components;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlayers() {
        return players;
    }

    public ServiceList getServices() {
        return services;
    }

    public Components getComponents() {
        return components;
    }

    public void setNumberOfPlayers(int numberOfPlayers){
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers(){
        return numberOfPlayers;
    }
}
