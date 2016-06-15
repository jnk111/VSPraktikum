package vs.malte.models;

/**
 * Spielerobjekt fuer ein erstelltes Spiel
 * 
 * TODO: PAWNs muessen im Spiel noch vergliechen werden, damit nicht mehrere Spieler die selbe Spielfigur haben.
 * 
 * @author maltrn
 *
 */
public class Player
{
    private String id;
    private String user;
    private boolean ready;
    private String pawn;
    private String account;
    private String uri;

    public Player( String user, String id, String pawn, String account, String uri, boolean ready )
    {
        super();
        this.user = user;
        this.id = id;
        this.pawn = pawn;
        this.account = account;
        this.ready = ready;
        this.uri = uri;
    }

    public Player()
    {
        this.ready = false;
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

    public boolean getReadyness()
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

    public boolean isReady()
    {
        return ready;
    }

    public void setReady( boolean ready )
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

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		
}
