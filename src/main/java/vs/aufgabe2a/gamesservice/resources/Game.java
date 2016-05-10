package vs.aufgabe2a.gamesservice.resources;

import java.util.HashMap;
import java.util.Map;

public class Game
{
    private String id;
    private String name;
    private String players;
    private boolean started;
    private ServiceList services;
    private Components components;
    private Map<String, Player> joinedPlayers;  // TODO playerMap soll nich in JSON angezeigt werden

    public Game()
    {
        started = false;       
        joinedPlayers = new HashMap<>();
        services = new ServiceList();
        components = new Components();
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getUserService()
    {
        return players;
    }

    public ServiceList getServiceList()
    {
        return services;
    }

    public Map<String, Player> getPlayers()
    {
        return joinedPlayers;
    }

    public Components getComponents()
    {
        return components;
    }

    public boolean isRunning()
    {
        return started;
    }
    
    public void setComponents( Components components )
    {
        this.components = components;
    }

    public void setRunning( boolean running )
    {
        this.started = running;
    }

    public void addPlayer( Player player )
    {
        this.joinedPlayers.put( player.getId(), player );
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setPlayers( String players )
    {
        this.players = players;
    }

    public void setServices( ServiceList services )
    {
        this.services = services;
    }

    public boolean isValid()
    {
        return true;                        // TODO isValid fuer Game-Klasse anpassen
    }

    @Override
    public String toString()
    {
        return ( "Name: " + getName() +
                ", ID: " + getId() +
                ", Players: " + getUserService() + "\n" +
                ", Services: " + getServiceList() +
                ", Player List: " + getUserService() );
    }
}
