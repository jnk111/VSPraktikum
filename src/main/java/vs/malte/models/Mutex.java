package vs.malte.models;

import java.util.concurrent.locks.ReentrantLock;

public class Mutex
{
    private String name;    
    private String permittedUser; // Spieler der die Erlaubnis hat, den Mutex zu belegen
    private String currentUser; // Spieler der die Erlaubnis hat, den Mutex zu belegen
    private ReentrantLock mutex;

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
    }

    public boolean acquire( String playerId )
    {
        boolean result = false;
        
        if(playerId.equals( permittedUser ))
        {
            this.currentUser = playerId;
            result = true;
        }
        
        return result;
    }
    
    public void release( String playerId )
    {
        if(playerId.equals( permittedUser ))
            this.currentUser = "";
    }
    
    public String getCurrentUser()
    {
        return this.currentUser;
    }
    
}
