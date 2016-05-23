package vs.malte.json;

import java.util.LinkedList;
import java.util.List;

public class GamesListDTO
{
    List<GameDTO> games;
    
    public GamesListDTO()
    {
        games = new LinkedList<>();
    }

    public List<GameDTO> getGameList()
    {
        return games;
    }

    public void setGameList( List<GameDTO> collection )
    {
        this.games = collection;
    }
}
