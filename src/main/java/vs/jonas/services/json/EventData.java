package vs.jonas.services.json;

public class EventData {

	private String game;
	private String type;
	private String name;
	private String reason;
	private String ressource;
	private String player;

	/**
	 * Initialisiert ein Event
	 * @param game Die URI des Games
	 * @param type Der interne Typ des Events
	 * @param name Der Name des Events
	 * @param reason Beschreibung des Ausloesers.
	 * @param ressource die URI der Ressource, die mit diesem Event zusammenhaengt
	 * @param player Die URI des Spielers der das Event ausgeloest hat.
	 */
	public EventData(String game, String type, String name, String reason, String ressource, String player) {
		super();
		this.game = game;
		this.type = type;
		this.name = name;
		this.reason = reason;
		this.ressource = ressource;
		this.player = player;
	}

	public String getGame() {
		return game;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getReason() {
		return reason;
	}

	public String getRessource() {
		return ressource;
	}

	public String getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return "Event [game=" + game + ", type=" + type + ", name=" + name + ", reason=" + reason + ", ressource="
				+ ressource + ", player=" + player + "]";
	}

}
