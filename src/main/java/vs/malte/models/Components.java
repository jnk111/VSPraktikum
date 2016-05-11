package vs.malte.models;

import java.util.HashMap;
import java.util.Map;

public class Components
{

    private String games;
    private String dice;
    private String board;
    private String bank;
    private String broker;
    private String decks;
    private String events;

    public Components( String games, String dice, String board, String bank, String broker, String decks, String events )
    {
        this.games = games;
        this.dice = dice;
        this.board = board;
        this.bank = bank;
        this.broker = broker;
        this.decks = decks;
        this.events = events;
    }

    public Components()
    {
    }

    public String getgames()
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
     * Gibt eine HashMap zurueck mit allen Komponenten die in der ServiceList-Klasse gespeichert sind.
     * 
     * @return HashMap mit allen genutzten Services
     */
    public Map<String, String> getAllComponents()
    {
        Map<String, String> components = new HashMap<>();
        
        if(games != null)
        components.put( "games", games );
        
        if(dice != null)
        components.put( "dice", dice );
        
        if(board != null)
        components.put( "board", board );
        
        if(bank != null)
        components.put( "bank", bank );
        
        if(broker != null)
        components.put( "broker", broker );
        
        if(decks != null)
        components.put( "decks", decks );
        
        if(events != null)
        components.put( "events", events );
        
        return components;
    }
    
    /**
     * Methode zu Setzen von neuen Komponenten in die Service-Klasse.
     * 
     * @param HashMap mit neuen Services
     */
    public void setAllComponents(Map<String, String> newComponents)
    {
        if(newComponents.containsKey( "games" ))
            this.games = newComponents.get( "games" );

        if(newComponents.containsKey( "dice" ))
            this.dice = newComponents.get( "dice" );

        if(newComponents.containsKey( "board" ))
            this.board = newComponents.get( "board" );

        if(newComponents.containsKey( "bank" ))
            this.bank = newComponents.get( "bank" );

        if(newComponents.containsKey( "broker" ))
            this.broker = newComponents.get( "broker" );

        if(newComponents.containsKey( "decks" ))
            this.decks = newComponents.get( "decks" );

        if(newComponents.containsKey( "events" ))
            this.events = newComponents.get( "events" );
        
    }

    public void setGames( String games )
    {
        this.games = games;
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
        return "ServiceList [games=" + games + ", dice=" + dice + ", board=" + board + ", bank=" + bank + ", broker=" + broker + ", decks=" + decks + ", events="
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
        // return ( getgames() == null || ServiceTest.isValidService( getgames() ) )
        // && ( getDice() == null || ServiceTest.isValidService( getDice() ) )
        // && ( getBoard() == null || ServiceTest.isValidService( getBoard() ) )
        // && ( getBank() == null || ServiceTest.isValidService( getBank() ) )
        // && ( getBroker() == null || ServiceTest.isValidService( getBroker() ) )
        // && ( getDecks() == null || ServiceTest.isValidService( getDecks() ) )
        // && ( getEvents() == null || ServiceTest.isValidService( getEvents() ) );

        return ( getgames() == null || getgames().matches( "/games" ) )
                && ( getDice() == null || getDice().matches( "/dice" ) )
                && ( getBoard() == null || getBoard().matches( "/board" ) )
                && ( getBank() == null || getBank().matches( "/bank" ) )
                && ( getBroker() == null || getBroker().matches( "/broker" ) )
                && ( getDecks() == null || getDecks().matches( "/decks" )
                        && ( getEvents() == null || getEvents().matches( "/events" ) ) );
    }
}
