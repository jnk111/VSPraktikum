package vs.jonas.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.json.Place;
import vs.jonas.client.json.PlayerInformation;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.model.tablemodel.GameFieldTableModel;
import vs.jonas.client.model.tablemodel.PlayerOverviewTableModel;
import vs.jonas.client.view.GameUI;

public class GameController {
	
	GameUI ui;
	RestopolyClient client;
	String gameID;
	
	public GameController(RestopolyClient client, String gameID) throws IOException, UnirestException{
		this.client = client;
		this.gameID = gameID;
		client.enterGame(gameID);
		initialisiereUI();
		
	}

	private void initialisiereUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ui = new GameUI();
					registriereActionListener();
					ladeSpielerInformationen();
					ladeGameFieldInformationen();
					ui.getUsername().setText(client.getUser().getName());
					ui.showUI();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Hier ist ein Kommunikationsfehler aufgetreten.");
					e.printStackTrace();
				}
			}
		});
	}
	
	private void registriereActionListener() {
		ui.getBtnWrfeln().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int number = client.rollDice();
					JOptionPane.showMessageDialog(null, "Wurfergebnis: " + number);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Service Offline");
//					e1.printStackTrace();
				}
			}
		});
	}
	
	private void ladeSpielerInformationen() throws Exception {
		PlayerOverviewTableModel model = (PlayerOverviewTableModel) ui.getPlayerTable().getModel();
		List<PlayerInformation> data = client.getPlayers(gameID);
		model.loadData(data);
	}
	

	private void ladeGameFieldInformationen() throws IOException, UnirestException {
		GameFieldTableModel model = (GameFieldTableModel) ui.getGameFIeldTable().getModel();
		List<Place> data = client.getPlaces(gameID);
		model.loadData(data);
		
	}
}
