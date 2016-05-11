package vs.jonas.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.view.RestopolyMenuUI;

public class MenuController {
	
	private RestopolyMenuUI ui;
	private RestopolyClient client;
	
	public MenuController(RestopolyClient client){
		this.client = client;
		initializeUI();
	}

	private void initializeUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ui = new RestopolyMenuUI();
					registerActionListener();
					ui.showUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	private void registerActionListener() {
		ui.getBtnOffeneSpiele().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new OffeneSpieleController(client);				
			}
		});
		
		ui.getBtnSpielErstellen().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String gameName = JOptionPane.showInputDialog("Name des Spiels:");
				if(gameName != null){
					if(!gameName.equals("")){
						try {
							client.createANewGame(gameName);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "Ungültiger Name. \nDer Name darf nicht leer sein.");
					}					
				}
			}
		});
	}
	
}
