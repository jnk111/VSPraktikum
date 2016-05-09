package vs.client.controller;

import vs.client.model.RestopolyClient;

public class GameController {
	
	RestopolyClient client;
	String gameID;
	
	public GameController(RestopolyClient client, String gameID){
		this.client = client;
		this.gameID = gameID;
	}
}
