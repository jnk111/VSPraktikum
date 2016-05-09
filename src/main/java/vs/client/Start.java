package vs.client;

import vs.client.controller.MenuController;
import vs.client.model.RestopolyClient;

public class Start {
	public static void main(String[] args) {
		RestopolyClient client = new RestopolyClient();
		new MenuController(client);
	}
}
