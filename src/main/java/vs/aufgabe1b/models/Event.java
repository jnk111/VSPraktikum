package vs.aufgabe1b.models;

import java.sql.Timestamp;

import com.google.gson.Gson;

public class Event {

	private String id;
	private String game;
	private String type;
	private String name;
	private String reason;
	private String ressource;
	private String player;
	private String time;
	
	/**
	 * Initialisiert ein Event
	 * @param game Die URI des Games
	 * @param type Der interne Typ des Events
	 * @param name Der Name des Events
	 * @param reason Beschreibung des Auslösers.
	 * @param ressource die URI der Ressource, die mit diesem Event zusammenhängt
	 * @param player Die URI des Spielers der das Event ausgelöst hat.
	 */
	public Event(String game, String type, String name, String reason, String ressource, String player) {
		super();
		this.game = game;
		this.type = type;
		this.name = name;
		this.reason = reason;
		this.ressource = ressource;
		this.player = player;
		this.time = new Timestamp(System.currentTimeMillis())+"";
		this.id = "events/"+name;
	}

	/**
	 * Liefert die URL zum Event auf dem Eventserver.
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Die URI des Games
	 * @return
	 */
	public String getGame() {
		return game;
	}

	/**
	 * Der interne Typ des Events
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Der Name des Events
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Beschreibung des Auslösers.
	 * @return
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * die URI der Ressource, die mit diesem Event zusammenhängt
	 * @return
	 */
	public String getRessource() {
		return ressource;
	}

	/**
	 * Die URI des Spielers der das Event ausgelöst hat.
	 * @return
	 */
	public String getPlayer() {
		return player;
	}

	/** 
	 * Der Zeitstempel, was das Event erstellt wurde.
	 * @return
	 */
	public String getTime() {
		return time;
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
