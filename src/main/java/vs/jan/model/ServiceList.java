package vs.jan.model;

public class ServiceList {
	
	// Services
	private String games;
	private String dice;
	private String boards;
	private String banks;
	private String broker;
	private String decks;
	private String events;
	private String users;
	private String client;
	
	// Host
	private String gamesHost;
	private String diceHost;
	private String boardHost;
	private String bankHost;
	private String brokerHost;
	private String decksHost;
	private String eventsHost;
	private String usersHost;
	private String clientHost;


	public ServiceList(String game, String dice, String board, String bank, String broker, String decks, String events, String users, String client) {
		this.games = game;
		this.dice = dice;
		this.boards = board;
		this.banks = bank;
		this.broker = broker;
		this.decks = decks;
		this.events = events;
		this.users = users;
	}

	public String getGame() {
		return games;
	}

	public String getDice() {
		return dice;
	}

	public String getBank() {
		return banks;
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

	

	public String getBoards() {
		return boards;
	}

	public void setBoards(String boards) {
		this.boards = boards;
	}

	public void setBank(String bank) {
		this.banks = bank;
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
		return "ServiceList [game=" + games + ", dice=" + dice + ", board=" + boards + ", banks=" + banks + ", broker="
				+ broker + ", decks=" + decks + ", events=" + events + "]";
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getGamesHost() {
		return gamesHost;
	}

	public void setGamesHost(String gamesHost) {
		this.gamesHost = gamesHost;
	}

	public String getDiceHost() {
		return diceHost;
	}

	public void setDiceHost(String diceHost) {
		this.diceHost = diceHost;
	}

	public String getBoardHost() {
		return boardHost;
	}

	public void setBoardHost(String boardHost) {
		this.boardHost = boardHost;
	}

	public String getBankHost() {
		return bankHost;
	}

	public void setBankHost(String bankHost) {
		this.bankHost = bankHost;
	}

	public String getBrokerHost() {
		return brokerHost;
	}

	public void setBrokerHost(String brokerHost) {
		this.brokerHost = brokerHost;
	}

	public String getDecksHost() {
		return decksHost;
	}

	public void setDecksHost(String decksHost) {
		this.decksHost = decksHost;
	}

	public String getEventsHost() {
		return eventsHost;
	}

	public void setEventsHost(String eventsHost) {
		this.eventsHost = eventsHost;
	}

	public String getUsersHost() {
		return usersHost;
	}

	public void setUsersHost(String usersHost) {
		this.usersHost = usersHost;
	}

	public String getClientHost() {
		return clientHost;
	}

	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public void initializeHosts() {
		this.gamesHost = this.games.replace("/games", "");
		this.diceHost = this.dice.replace("/dice", "");
		this.boardHost = this.boards.replace("/boards", "");
		this.bankHost = this.banks.replace("/banks", "");
		this.brokerHost = this.broker.replace("/broker", "");
		this.decksHost = this.decks.replace("/decks", "");
		this.eventsHost = this.events.replace("/events", "");
		this.usersHost = this.users.replace("/users", "");
		this.clientHost = this.client.replace("/client", "");
		
	}
	
	
}
