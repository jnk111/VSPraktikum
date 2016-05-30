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
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;
import vs.jonas.client.model.table.tablemodel.PlayerOverviewTableModel;
import vs.jonas.client.view.GameUI;

/**
 * Diese Klasse implementiert einen Controller für die GamesUI.
 * Er nimmt Nutzeranfragen entgegen und leitet sie weiter.
 * @author Jones
 *
 */
public class GameController {
	
	GameUI ui;
	RestopolyClient client;
	String gameID;
	
	/**
	 * Initialisiert den Controller
	 * @param client Der Client, der mit den Services kommuniziert
	 * @param gameID Die ID des Games
	 * @throws IOException
	 * @throws UnirestException
	 */
	public GameController(RestopolyClient client, String gameID) throws IOException, UnirestException{
		this.client = client;
		this.gameID = gameID;
		client.enterGame(gameID);
		initialisiereUI();
		
	}

	/**
	 * Initialisiert die UI
	 */
	private void initialisiereUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ui = new GameUI();
					registriereActionListener();
					updateGame();
					ui.getUsername().setText(client.getUser().getName());
					ui.showUI();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Hier ist ein Kommunikationsfehler aufgetreten.");
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Registriert die Listener an der UI
	 */
	private void registriereActionListener() {
		ui.getBtnStarten().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		ui.getBtnWrfeln().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int number = client.rollDice(gameID);
					JOptionPane.showMessageDialog(null, "Wurfergebnis: " + number);
					updateGame();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Service Offline");
//					e1.printStackTrace();
				} catch (UnirestException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void updateGame() throws Exception{
		ladeSpielerInformationen();
		ladeGameFieldInformationen();
	}
	
	/**
	 * Laedt die Spieler-Informationen aller Teilnehmenden Spieler.
	 * @throws Exception
	 */
	private void ladeSpielerInformationen() throws Exception {
		PlayerOverviewTableModel model = (PlayerOverviewTableModel) ui.getPlayerTable().getModel();
		List<PlayerInformation> data = client.getPlayers(gameID);
		model.loadData(data);
	}
	
	/**
	 * Laedt die Spielfeld-Informationen
	 * @throws IOException
	 * @throws UnirestException
	 */
	private void ladeGameFieldInformationen() throws IOException, UnirestException {
		GameFieldTableModel model = (GameFieldTableModel) ui.getGameFIeldTable().getModel();
		List<Place> data = client.getPlaces(gameID);
		model.loadData(data);
		
	}
}
