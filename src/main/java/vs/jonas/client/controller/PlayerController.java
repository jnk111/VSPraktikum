package vs.jonas.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.json.Place;
import vs.jonas.client.json.User;
import vs.jonas.client.model.Player;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.model.ShowMessageThread;
import vs.jonas.client.model.table.tablemodel.PlayersPlacesTableModel;
import vs.jonas.client.view.PlayerUI;
import vs.jonas.exceptions.EstateAlreadyOwnedException;

public class PlayerController {

	private RestopolyClient client;
	private PlayerUI ui;
	private Player player;
	private User user;
	private String gameID;
	
	public PlayerController(RestopolyClient client, Player player, String gameID, User user){
		this.client = client;
		this.player = player;
		this.user = user;
		this.gameID = gameID;
		System.out.println("Neuer PlayerController....");
		System.out.println("Player: " + player.getUri());
		System.out.println("Client: " + user.getPlayerUri());
		this.ui = new PlayerUI(this.player, user.getPlayerUri().equals(player.getUri()));
		registriereListener();
		this.ui.showUI();
	}

	private void registriereListener() {
		ui.getBtnKaufanfrage().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = ui.getTable().getSelectedRow();
				
				if(selectedRow != -1){
					PlayersPlacesTableModel model = (PlayersPlacesTableModel) ui.getTable().getModel();
					Place place = model.getPlace(selectedRow);
					try {
						client.sendTradeRequest(place.getBroker(),user.getName());
					} catch (UnirestException e1) {
						e1.printStackTrace();
					}
				} else{
					new ShowMessageThread("Es wurde kein Grundstück ausgewählt.").start();
				}
			}
		});
		
		ui.getBtnHausKaufen().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = ui.getTable().getSelectedRow();
				
				if(selectedRow != -1){
					PlayersPlacesTableModel model = (PlayersPlacesTableModel) ui.getTable().getModel();
					Place place = model.getPlace(selectedRow);
					try {
						client.buyEstate(gameID, place, user);
					} catch (UnirestException e1) {
						e1.printStackTrace();
					} catch (EstateAlreadyOwnedException e1) {
						e1.printStackTrace();
					}
				} else{
					new ShowMessageThread("Es wurde kein Grundstück ausgewählt.").start();
				}
			}
		});
		
	}
}
