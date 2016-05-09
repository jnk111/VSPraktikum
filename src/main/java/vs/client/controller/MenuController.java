package vs.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import vs.client.model.RestopolyClient;
import vs.client.view.RestopolyMenuUI;

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
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
