package vs.malte.services;

import java.util.HashMap;
import java.util.Map;

import vs.malte.models.Mutex;

public class MutexService
{
    private Map<String, Mutex> mutexMap;    // Map zum Speichern der Mutexe fuer die einzelnen Spiele

    public MutexService()
    {
        mutexMap = new HashMap<>();
    }

    public void newGameMutex( String gameId )
    {
        Mutex newMutex = new Mutex();

        mutexMap.put( gameId, newMutex );   // TODO Fehlerbehandlung
    }

    public void assignMutexPermission( String gameId, String playerId )
    {
        mutexMap.get( gameId ).setPermissionForAcquireMutex( playerId );
    }

    public boolean acquire( String gameId, String playerId )
    {
        return mutexMap.get( gameId ).acquire( playerId );
    }

    public void release( String gameId, String playerId )
    {
        mutexMap.get( gameId ).acquire( playerId );
    }

    /**
     * Gibt die UserID des Users zurueck der momentan den Mutex haelt
     * 
     * @param gameId
     * @return  userId
     */
    public String getMutexUser( String gameId )
    {
        return mutexMap.get( gameId ).getCurrentUser();
    }
}
