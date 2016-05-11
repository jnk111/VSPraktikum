package vs.jonas.client;

import vs.jonas.client.controller.MenuController;
import vs.jonas.client.model.RestopolyClient;

public class Start {
	public static void main(String[] args) {
		RestopolyClient client = new RestopolyClient();
		new MenuController(client);
	}
}
