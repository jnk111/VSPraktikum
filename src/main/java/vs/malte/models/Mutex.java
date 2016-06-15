package vs.malte.models;

public class Mutex
{
    private String name;
    private String permittedUser; // Spieler der die Erlaubnis hat, den Mutex zu belegen
    private String mutexUser; // Spieler der zur Zeit den Mutex hat

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

    public boolean acquire( String playerId )
    {
        boolean result = false;

        this.mutexUser = playerId;
        result = true;

        if ( DEBUG_MODE )
            System.out.println( playerId + " hat die Erlaubnis den Mutex anzufordern." );

        return result;
    }

    public void release( String playerId )
    {
        if ( playerId.equals( mutexUser ) )
        {
            this.mutexUser = "";
            this.permittedUser = "";
        }

        if ( DEBUG_MODE )
            System.out.println( playerId + "hat Mutex abgegeben." );
    }

    public String getCurrentUser()
    {
        return this.mutexUser;
    }

    public String getPermittedUser()
    {
        return this.permittedUser;
    }

}
