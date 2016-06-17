package vs.jonas.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import vs.jonas.client.model.Player;
import vs.jonas.client.view.PlayerUI;

public class PlayerController {

	private GameController controller;
	private PlayerUI ui;
	private Player player;
	
	public PlayerController(GameController controller, Player player){
		this.controller = controller;
		this.player = player;
		this.ui = new PlayerUI(this.player);
		registriereListener();
		this.ui.showUI();
	}

	private void registriereListener() {
		ui.getBtnKaufanfrage().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.buyRequest(player);
			}
		});
	}
}
