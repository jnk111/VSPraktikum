package vs.malte.json;

public class CurrentPlayerDTO
{
    private String id;
    private String user;
    private String pawn;
    private String account;
    private boolean ready;
    
    public CurrentPlayerDTO()
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

    public String getUser()
    {
        return user;
    }

    public void setUser( String user )
    {
        this.user = user;
    }

    public String getPawn()
    {
        return pawn;
    }

    public void setPawn( String pawn )
    {
        this.pawn = pawn;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount( String account )
    {
        this.account = account;
    }

    public boolean isReady()
    {
        return ready;
    }

    public void setReady( boolean ready )
    {
        this.ready = ready;
    }
    
    
}
