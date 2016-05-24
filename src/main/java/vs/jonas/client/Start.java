package vs.jonas.client;

import vs.jonas.client.controller.MenuController;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.services.services.YellowPagesService;

public class Start {
	public static void main(String[] args) {
		YellowPagesService yellowPagesService = new YellowPagesService(YellowPagesService.LOCAL_SERVICES);
		RestopolyClient client = new RestopolyClient(yellowPagesService);
		new MenuController(client);
	}
}
