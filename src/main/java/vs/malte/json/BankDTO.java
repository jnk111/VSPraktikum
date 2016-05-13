package vs.malte.json;

public class BankDTO
{
    private String game;

    public BankDTO( String game )
    {
        this.game = game;
    }

    public BankDTO()
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
