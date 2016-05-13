package vs.malte.json;

import java.util.ArrayList;
import java.util.List;

import vs.malte.models.Player;

public class PlayerDTO
{
    List<String> players;
    
    public PlayerDTO()
    {
        players = new ArrayList<>();
    }

    public List<String> getPlayers()
    {
        return players;
    }

    public void setPlayers( List<String> players )
    {
        this.players = players;
    }
}
