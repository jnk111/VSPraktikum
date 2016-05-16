package vs.malte.json;

public class BrokerDTO
{
    private String game;

    public BrokerDTO( String game )
    {
        this.game = game;
    }

    public BrokerDTO()
    {
    }

    public String getGame()
    {
        return game;
    }

    public void setGame( String game )
    {
        this.game = game;
    }
}
