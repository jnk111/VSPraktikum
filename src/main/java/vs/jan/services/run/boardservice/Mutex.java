package vs.jan.services.run.boardservice;

import java.util.concurrent.locks.ReentrantLock;

public class Mutex
{
    private String name;
    private String permittedUser; // Spieler der die Erlaubnis hat, den Mutex zu belegen
    private String currentUser; // Spieler der zur Zeit den Mutex hat
    private ReentrantLock mutex;

    private final boolean DEBUG_MODE = true;

    public Mutex()
    {
        mutex = new ReentrantLock();
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
        this.currentUser = player;
    }

    public boolean acquire( String playerId )
    {
        boolean result = false;

        if ( playerId.equals( permittedUser ) )
        {
            this.permittedUser = "";
            this.currentUser = playerId;
            result = true;

            if ( DEBUG_MODE )
                System.out.println( playerId + " hat die Erlaubnis den Mutex anzufordern." );
        }

        return result;
    }

    public void release( String playerId )
    {
        if ( playerId.equals( permittedUser ) )
            this.currentUser = "";

        if ( DEBUG_MODE )
            System.out.println( playerId + "hat Mutex abgegeben." );
    }

    public String getCurrentUser()
    {
        return this.currentUser;
    }

}
