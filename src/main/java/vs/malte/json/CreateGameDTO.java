package vs.malte.json;

/**
 * GameDTO zum Erstellen eines neuen Spiels.
 * 
 * 
 * @author maltrn
 *
 */
public class CreateGameDTO
{
    String name;

    public CreateGameDTO()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

}
