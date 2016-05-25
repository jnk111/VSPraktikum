package vs.jan.services.run.boardservice;

import java.util.HashMap;
import java.util.Map;

import vs.malte.models.Mutex;

public class MutexServiceFIXED
{
    private Map<String, Mutex> mutexMap;    // Map zum Speichern der Mutexe fuer die einzelnen Spiele

    public MutexServiceFIXED()
    {
        mutexMap = new HashMap<>();
    }

    /**
     * Erstellt ein neues Mutexobjekt fuer ein Spielt
     * 
     * @param gameId
     */
    public void newGameMutex( String gameId )
    {
        Mutex newMutex = new Mutex();

        mutexMap.put( gameId, newMutex );   // TODO Fehlerbehandlung
    }

    /**
     * Vergibt die Erlaubnis den Mutex in Anspruch zunehmen
     * 
     * @param gameId
     * @param playerId
     */
    public void assignMutexPermission( String gameId, String playerId )
    {
        mutexMap.get( gameId ).setPermissionForAcquireMutex( playerId );
    }

    /**
     * Vergibt den Mutex an einen Spieler, sofern dieser Spieler die Erlaubnis dazu hat.
     * Erlaubnis wird mit "assignMutexPermission" vergeben.
     * 
     * @param GameID des jeweiligen Spiels
     * @param PlayerID des Players
     * @return true falls Mutex erfolgreich uebergeben wurde ansonsten false
     */
    public boolean acquire( String gameId, String playerId )
    {
        return mutexMap.get( gameId ).acquire( playerId );
    }

    /**
     * Gibt Mutex fuer das jeweilige Spiel und den jeweiligen Spieler frei.
     * 
     * @param gameId
     * @param playerId
     */
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
