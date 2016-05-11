package vs.malte.json;

public class BoardDTO
{
    private String game;

    public BoardDTO( String game )
    {
        this.game = game;
    }

    public BoardDTO()
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
