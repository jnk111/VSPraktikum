package vs.malte.json;

public class AllPlayersArrayDTO
{
    AllPlayersDTO[] players;
    
    public AllPlayersArrayDTO(int numberOfPlayers)
    {
        players = new AllPlayersDTO[numberOfPlayers];
    }

    public AllPlayersDTO[] getPlayers()
    {
        return this.players;
    }

    public void setPlayers( Object[] objects )
    {
        this.players = (AllPlayersDTO[]) objects;
    }
}
