package vs.jonas.services.model;

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
	private Timestamp time;
	
	/**
	 * Initialisiert ein Event
	 * @param game Die URI des Games
	 * @param type Der interne Typ des Events
	 * @param name Der Name des Events
	 * @param reason Beschreibung des Ausloesers.
	 * @param ressource die URI der Ressource, die mit diesem Event zusammenhaengt
	 * @param player Die URI des Spielers der das Event ausgeloest hat.
	 */
	public Event(String game, String type, String name, String reason, String ressource, String player) {
		super();
		this.game = game;
		this.type = type;
		this.name = name;
		this.reason = reason;
		this.ressource = ressource;
		this.player = player;
		this.time = new Timestamp(System.currentTimeMillis());
		this.id = "events/"+name;
	}
	
	public synchronized void setID(String id){
		this.id = id;
	}

	/**
	 * Liefert die URL zum Event auf dem Eventserver.
	 * @return
	 */
	public synchronized String getId() {
		return id;
	}

	/**
	 * Die URI des Games
	 * @return
	 */
	public synchronized String getGame() {
		return game;
	}

	/**
	 * Der interne Typ des Events
	 * @return
	 */
	public synchronized String getType() {
		return type;
	}

	/**
	 * Der Name des Events
	 * @return
	 */
	public synchronized String getName() {
		return name;
	}

	/**
	 * Beschreibung des Ausl�sers.
	 * @return
	 */
	public synchronized String getReason() {
		return reason;
	}

	/**
	 * die URI der Ressource, die mit diesem Event zusammenh�ngt
	 * @return
	 */
	public synchronized String getRessource() {
		return ressource;
	}

	/**
	 * Die URI des Spielers der das Event ausgel�st hat.
	 * @return
	 */
	public synchronized String getPlayer() {
		return player;
	}

	/** 
	 * Der Zeitstempel, was das Event erstellt wurde.
	 * @return
	 */
	public synchronized Timestamp getTime() {
		return time;
	}
	
	@Override
	public synchronized String toString(){
		return new Gson().toJson(this);
		
	}

	@Override
	public synchronized int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime * result + ((ressource == null) ? 0 : ressource.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public synchronized boolean equals(Object obj) {
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
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		if (ressource == null) {
			if (other.ressource != null)
				return false;
		} else if (!ressource.equals(other.ressource))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


	
}
