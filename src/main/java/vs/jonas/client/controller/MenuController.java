package vs.jonas.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.view.RestopolyMenuUI;

/**
 * Diese Klasse implementiert einen Controller für die MenuUI.
 * Sie ist der Hauptcontroller und Startpunkt der Anwendung.
 * Von hier aus werden alle weiteren Controller gestartet.
 * 
 * @author Jones
 *
 */
public class MenuController {
	
	private RestopolyMenuUI ui;
	private RestopolyClient client;
	
	/**
	 * Initialisiert den Controller
	 * @param client
	 * @throws Exception
	 */
	public MenuController(RestopolyClient client) throws Exception{
		this.client = client;
		initializeUI();
	}

	/**
	 * Initialisiert die UI.
	 */
	private void initializeUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ui = new RestopolyMenuUI();
					registerActionListener();
					ui.showUI();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Fehler. Der Service konnte nicht erreicht werden.");
				}
			}
		});
		
	}

	/**
	 * Registriert die Listener an der UI.
	 */
	private void registerActionListener() {
		ui.getBtnOffeneSpiele().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new OffeneSpieleController(client);				
				ui.getFrame().dispose();
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
						} catch (UnirestException e1) {
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
