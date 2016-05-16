package vs.malte.json;

public class PawnDTO
{
    private String id;
    private String player;
    private String place;
    private int position;
    private String roll;
    private String move;
    
    public PawnDTO()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getPlayer()
    {
        return player;
    }

    public void setPlayer( String player )
    {
        this.player = player;
    }

    public String getPlace()
    {
        return place;
    }

    public void setPlace( String place )
    {
        this.place = place;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public String getRoll()
    {
        return roll;
    }

    public void setRoll( String roll )
    {
        this.roll = roll;
    }

    public String getMove()
    {
        return move;
    }

    public void setMove( String move )
    {
        this.move = move;
    }
    
    
    
}
