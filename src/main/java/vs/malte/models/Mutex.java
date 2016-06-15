package vs.malte.models;

public class Mutex
{
    private String name;
    private String permittedUser; // Spieler der die Erlaubnis hat, den Mutex zu belegen
    private String currentUser; // Spieler der zur Zeit den Mutex hat

    private final boolean DEBUG_MODE = false;

    public Mutex()
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

    public void setPermissionForAcquireMutex( String player )
    {
        this.permittedUser = player;
    }

    public boolean acquire( String playerId )
    {
        boolean result = false;

        this.currentUser = playerId;
        result = true;

        if ( DEBUG_MODE )
            System.out.println( playerId + " hat die Erlaubnis den Mutex anzufordern." );

        return result;
    }

    public void release( String playerId )
    {
        if ( playerId.equals( currentUser ) )
        {
            this.currentUser = "";
            this.permittedUser = "";
        }

        if ( DEBUG_MODE )
            System.out.println( playerId + "hat Mutex abgegeben." );
    }

    public String getCurrentUser()
    {
        return this.currentUser;
    }

    public String getPermittedUser()
    {
        return this.permittedUser;
    }

}
