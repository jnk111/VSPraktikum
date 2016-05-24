package vs.jonas.client.json;

/**
 * Created by Jones on 24.05.2016.
 */
public class Components {

    private String games;
    private String dice;
    private String board;
    private String bank;
    private String broker;
    private String decks;
    private String events;

    public Components( String games, String dice, String board, String bank, String broker, String decks, String events )
    {
        this.games = games;
        this.dice = dice;
        this.board = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
        this.events = events;
    }

    public Components()
    {
    }

    public String getgames()
    {
        return games;
    }

    public String getDice()
    {
        return dice;
    }

    public String getBoard()
    {
        return board;
    }

    public String getBank()
    {
        return bank;
    }

    public String getBroker()
    {
        return broker;
    }

    public String getDecks()
    {
        return decks;
    }

    public String getEvents()
    {
        return events;
    }
}
