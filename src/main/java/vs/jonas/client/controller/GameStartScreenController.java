package vs.jonas.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.json.PlayerInformation;
import vs.jonas.client.json.User;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.model.table.tablemodel.PlayerStartGameScreenModel;
import vs.jonas.client.view.StartGameUI;

public class GameStartScreenController {

	private StartGameUI ui;
	private RestopolyClient client;
	private String gameID;
	private User user;
	
	public GameStartScreenController(RestopolyClient client, String gameID, User user){
		this.client = client;
		this.gameID = gameID;
		this.user = user;
		initialisiereUI();
		
	}

	private void initialisiereUI() {
		EventQueue.invokeLater(new Runnable() {	
			public void run() {
				try {
					ui = new StartGameUI();
					registriereActionListener();
					updateUI();
					ui.showUI();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Hier ist ein Kommunikationsfehler aufgetreten.");
					e.printStackTrace();
				}
			}


		});
	}

	private void registriereActionListener() {
		ui.getBtnStartGame().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.setReady(gameID, user);
					if(client.allPlayersReady(gameID)){
						ui.getFrame().dispose();
						new GameController(client,gameID,user);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});;
		
	}
	
	private void updateUI() throws IOException, UnirestException, Exception {
		PlayerStartGameScreenModel model = (PlayerStartGameScreenModel) ui.getTable().getModel();
		List<PlayerInformation> data = client.getPlayers(gameID);
		model.loadData(data);
	}
}
