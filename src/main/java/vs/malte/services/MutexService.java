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

    /**
     * Erstellt ein neues Mutexobjekt fuer ein Spiel
     * 
     * @param gameId
     */
    public void newGameMutex( String gameId )
    {
        Mutex newMutex = new Mutex();

        mutexMap.put( gameId, newMutex );   // TODO Fehlerbehandlung
    }

    /**
     * Vergibt den Mutex an einen Spieler, sofern dieser Spieler die Erlaubnis dazu hat. Erlaubnis wird mit "assignMutexPermission" vergeben.
     * 
     * @param GameID
     *            des jeweiligen Spiels
     * @param PlayerID
     *            des Players
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
        mutexMap.get( gameId ).release( playerId );
    }

    /**
     * Gibt die UserID des Users zurueck der momentan den Mutex haelt
     * 
     * @param gameId
     * @return userId
     */
    public String getMutexUser( String gameId )
    {
        return mutexMap.get( gameId ).getCurrentUser();
    }

    public String getMutexPermittedUser( String gameId )
    {
        return mutexMap.get( gameId ).getPermittedUser();
    }
}
