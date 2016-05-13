package vs.malte.json;

public class DecksDTO
{
    private String game;

    public DecksDTO( String game )
    {
        this.game = game;
    }

    public DecksDTO()
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
