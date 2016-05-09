package vs.client.controller;

import java.awt.EventQueue;

import vs.client.model.RestopolyClient;
import vs.client.view.GameUI;

public class GameController {
	
	RestopolyClient client;
	String gameID;
	
	public GameController(RestopolyClient client, String gameID){
		this.client = client;
		this.gameID = gameID;
		initialisiereUI();
	}

	private void initialisiereUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameUI ui = new GameUI();
					ui.showUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
