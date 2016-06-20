package vs.jonas.client;

import javax.swing.UIManager;

import vs.jonas.client.controller.MenuController;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.services.services.YellowPagesService;

public class Start {
	public static void main(String[] args) throws Exception {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		YellowPagesService yellowPagesService = new YellowPagesService(true);		
		 RestopolyClient client = new RestopolyClient(yellowPagesService);
		new MenuController(client);
	}
}
