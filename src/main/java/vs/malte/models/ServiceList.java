package vs.malte.models;

import java.util.HashMap;
import java.util.Map;

public class ServiceList
{
    private String games;
    private String dice;
    private String boards;
    private String bank;
    private String broker;
    private String decks;
    private String events;
    private String users;

    public ServiceList( String game, String dice, String board, String bank, String broker, String decks, String events )
    {
        this.games = game;
        this.dice = dice;
        this.boards = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
        this.events = events;
    }

    public ServiceList()
    {
    }

    public String getGame()
    {
        return games;
    }

    public String getDice()
    {
        return dice;
    }

    public String getBoard()
    {
        return boards;
    }

    public String getBank()
    {
        return bank;
    }

    public String getBroker()
    {
        return broker;
    }

    public String getDecks()
    {
        return decks;
    }

    public String getEvents()
    {
        return events;
    }

    /**
     * Gibt eine HashMap zurueck mit allen Services die in der ServiceList-Klasse gespeicht sind.
     * 
     * @return HashMap mit allen genutzten Services
     */
    public Map<String, String> getAllServices()
    {
        Map<String, String> services = new HashMap<>();

        if ( games != null )
            services.put( "games", games );

        if ( dice != null )
            services.put( "dice", dice );

        if ( boards != null )
            services.put( "boards", boards );

        if ( bank != null )
            services.put( "bank", bank );

        if ( broker != null )
            services.put( "broker", broker );

        if ( decks != null )
            services.put( "decks", decks );

        if ( events != null )
            services.put( "events", events );
        
        if ( events != null )
            services.put( "users", users );

        return services;
    }

    /**
     * Methode zu Setzen von neuen Services in die Service-Klasse.
     * 
     * @param HashMap
     *            mit neuen Services
     */
    public void setAllServices( Map<String, String> newServices )
    {
        if ( newServices.containsKey( "games" ) && this.games == null )
            setGame( newServices.get( "games" ) );

        if ( newServices.containsKey( "dice" ) && this.dice == null )
            setDice( newServices.get( "dice" ) );

        if ( newServices.containsKey( "boards" ) && this.boards == null )
            setBoard( newServices.get( "boards" ) );

        if ( newServices.containsKey( "bank" ) && this.bank == null )
            setBank( newServices.get( "bank" ) );

        if ( newServices.containsKey( "broker" ) && this.broker == null )
            setBroker( newServices.get( "broker" ) );

        if ( newServices.containsKey( "decks" ) && this.decks == null )
            setDecks( newServices.get( "decks" ) );

        if ( newServices.containsKey( "events" ) && this.events == null )
            setEvents( newServices.get( "events" ) );
        
        if ( newServices.containsKey( "users" ) && this.users == null )
            setUsers( newServices.get( "users" ) );

    }

    public void setGame( String game )
    {
        this.games = game;
    }

    public void setDice( String dice )
    {
        this.dice = dice;
    }

    public void setBoard( String board )
    {
        this.boards = board;
    }

    public void setBank( String bank )
    {
        this.bank = bank;
    }

    public void setBroker( String broker )
    {
        this.broker = broker;
    }

    public void setDecks( String decks )
    {
        this.decks = decks;
    }

    public void setEvents( String events )
    {
        this.events = events;
    }

    @Override
    public String toString()
    {
        return "ServiceList [game=" + games + ",\n"
                + " dice=" + dice + ",\n"
                + " board=" + boards + ",\n"
                + " bank=" + bank + ",\n"
                + " broker=" + broker + ",\n"
                + " decks=" + decks + ",\n"
                + " events=" + events + "]";

    }

    /**
     * Falls die jeweiligen Felder nicht leer sind wird geprueft ob die URLs gueltig sind. TODO: URLs pruefen, entweder ueber Regex die gueltige Yellow-Pages-Adresse(funktioniert
     * erst wenn wir alle Services richtig anmelden) und/oder ueber erfolgreiche Response-Codes(die zurueck gegeben werden, falls der Service erreichbar ist)
     * 
     * @return
     */
    public boolean isValid()
    {
        // return ( getGame() == null || ServiceTest.isValidService( getGame() ) )
        // && ( getDice() == null || ServiceTest.isValidService( getDice() ) )
        // && ( getBoard() == null || ServiceTest.isValidService( getBoard() ) )
        // && ( getBank() == null || ServiceTest.isValidService( getBank() ) )
        // && ( getBroker() == null || ServiceTest.isValidService( getBroker() ) )
        // && ( getDecks() == null || ServiceTest.isValidService( getDecks() ) )
        // && ( getEvents() == null || ServiceTest.isValidService( getEvents() ) );

        return ( getGame() == null || getGame().matches( "/games" ) )
                && ( getDice() == null || getDice().matches( "/dice" ) )
                && ( getBoard() == null || getBoard().matches( "/board" ) )
                && ( getBank() == null || getBank().matches( "/bank" ) )
                && ( getBroker() == null || getBroker().matches( "/broker" ) )
                && ( getDecks() == null || getDecks().matches( "/decks" )
                        && ( getEvents() == null || getEvents().matches( "/events" ) ) );
    }

    public String getGames()
    {
        return games;
    }

    public void setGames( String games )
    {
        this.games = games;
    }

    public String getUsers()
    {
        return users;
    }

    public void setUsers( String users )
    {
        this.users = users;
    }
    
    

}
