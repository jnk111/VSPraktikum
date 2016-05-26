package vs.jan.model;

public class ServiceList {
	private String games;
	private String dice;
	private String board;
	private String bank;
	private String broker;
	private String decks;
	private String events;

	public ServiceList() {
		this(null, null, null, null, null, null, null);
	}

	public ServiceList(String game, String dice, String board, String bank, String broker, String decks, String events) {
		this.games = game;
		this.dice = dice;
		this.board = board;
		this.bank = bank;
		this.broker = broker;
		this.decks = decks;
		this.events = events;
	}

	public String getGame() {
		return games;
	}

	public String getDice() {
		return dice;
	}

	public String getBoard() {
		return board;
	}

	public String getBank() {
		return bank;
	}

	public String getBroker() {
		return broker;
	}

	public String getDecks() {
		return decks;
	}

	public String getEvents() {
		return events;
	}
	
	

	public String getGames() {
		return games;
	}

	public void setGames(String games) {
		this.games = games;
	}

	public void setDice(String dice) {
		this.dice = dice;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public void setDecks(String decks) {
		this.decks = decks;
	}

	public void setEvents(String events) {
		this.events = events;
	}

	@Override
	public String toString() {
		return "ServiceList [game=" + games + ", dice=" + dice + ", board=" + board + ", bank=" + bank + ", broker="
				+ broker + ", decks=" + decks + ", events=" + events + "]";
	}
}
