package vs.jan.json.boardservice;

public class JSONPlayersList
{
    JSONPlayersListElement[] players;
    
    public JSONPlayersList(int numberOfPlayers)
    {
        players = new JSONPlayersListElement[numberOfPlayers];
    }

    public JSONPlayersListElement[] getPlayers()
    {
        return this.players;
    }

    public void setPlayers( Object[] objects )
    {
        this.players = (JSONPlayersListElement[]) objects;
    }
}
