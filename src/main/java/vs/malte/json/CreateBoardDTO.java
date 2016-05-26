package vs.malte.json;

public class CreateBoardDTO
{
    private String game;

    public CreateBoardDTO( String game )
    {
        this.game = game;
    }

    public CreateBoardDTO()
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
