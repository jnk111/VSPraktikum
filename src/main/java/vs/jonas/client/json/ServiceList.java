package vs.jonas.client.json;

import com.google.gson.Gson;

/**
 * Created by Jones on 24.05.2016.
 */
public class ServiceList {

    private String games;
    private String dice;
    private String board;
    private String bank;
    private String broker;
    private String decks;
    private String events;

    public ServiceList(String games, String dice, String board, String bank, String broker, String decks, String events) {
        this.games = games;
        this.dice = dice;
        this.board = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
        this.events = events;
    }

    public String getGames() {
        return games;
    }

    public String getDice() {
        return dice;
    }

    public String getBoard() {
        return board;
    }

    public String getBank() {
        return bank;
    }

    public String getBroker() {
        return broker;
    }

    public String getDecks() {
        return decks;
    }

    public String getEvents() {
        return events;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
