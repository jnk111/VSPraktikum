package vs.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import vs.client.model.RestopolyClient;
import vs.client.view.GameUI;

public class GameController {
	
	GameUI ui;
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
					ui = new GameUI();
					registriereActionListener();
					ui.showUI();
				} catch (Exception e) {
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
					e1.printStackTrace();
				}
			}
		});
	}
}
