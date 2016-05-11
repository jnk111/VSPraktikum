package vs.malte.models;

import java.util.HashMap;
import java.util.Map;

public class ServiceList
{
    private String games;
    private String dice;
    private String board;
    private String bank;
    private String broker;
    private String decks;
    private String events;

    public ServiceList( String game, String dice, String board, String bank, String broker, String decks, String events )
    {
        this.games = game;
        this.dice = dice;
        this.board = board;
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
        return board;
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

        if ( board != null )
            services.put( "board", board );

        if ( bank != null )
            services.put( "bank", bank );

        if ( broker != null )
            services.put( "broker", broker );

        if ( decks != null )
            services.put( "decks", decks );

        if ( events != null )
            services.put( "events", events );

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
        if ( newServices.containsKey( "games" ) )
            setGame( newServices.get( "games" ) );

        if ( newServices.containsKey( "dice" ) )
            setDice( newServices.get( "dice" ) );

        if ( newServices.containsKey( "board" ) )
            setBoard( newServices.get( "board" ) );

        if ( newServices.containsKey( "bank" ) )
            setBank( newServices.get( "bank" ) );

        if ( newServices.containsKey( "broker" ) )
            setBroker( newServices.get( "broker" ) );

        if ( newServices.containsKey( "decks" ) )
            setDecks( newServices.get( "decks" ) );

        if ( newServices.containsKey( "events" ) )
            setEvents( newServices.get( "events" ) );

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
        this.board = board;
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
        return "ServiceList [game=" + games + ", dice=" + dice + ", board=" + board + ", bank=" + bank + ", broker=" + broker + ", decks=" + decks + ", events="
                + events + "]";
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

}
