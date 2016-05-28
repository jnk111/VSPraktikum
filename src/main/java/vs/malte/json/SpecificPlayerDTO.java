package vs.malte.json;

import vs.malte.models.Player;

public class SpecificPlayerDTO
{

    private String id;
    private String user;
    private String ready;
    private String pawn;
    private String account;

    public SpecificPlayerDTO( String user, String id, String pawn, String account, String ready )
    {
        super();
        this.user = user;
        this.id = id;
        this.pawn = pawn;
        this.account = account;
        this.ready = ready;
    }

    public SpecificPlayerDTO()
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

    public String getUserName()
    {
        return user;
    }

    public void setUserName( String user )
    {
        this.user = user;
    }

    public String getReadyness()
    {
        return ready;
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

    public String isReady()
    {
        return ready;
    }

    public void setReady( String ready )
    {
        this.ready = ready;
    }

    public boolean isValid()
    {
        return getId() != null &&
                getUserName() != null &&
                getAccount() != null &&
                getPawn() != null;
    }

    @Override
    public String toString()
    {
        return "Player [id=" + id + ", user=" + user + ", pawn=" + pawn + ", account=" + account + "]";
    }

    @Override
    public boolean equals( Object that )
    {
        boolean result = false;

        if ( that instanceof Player )
        {
            Player thatPlayer = (Player) that;

            result = ( this.getUserName().equals( thatPlayer.getUserName() ) ||
                    this.getId().equals( thatPlayer.getId() ) );
        }

        return result;
    }

}
