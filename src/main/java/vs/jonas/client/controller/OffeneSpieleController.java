package vs.jonas.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.json.GameResponse;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.model.tablemodel.GameInformationTableModel;
import vs.jonas.client.view.OffeneSpieleUI;

/**
 * Diese Klasse kontrolliert die Aktionen auf der OffeneSpieleUI,
 * verarbeitet die Aktivit�ten des Users und kann ein neues Spiel starten.
 * @author Jones
 */
public class OffeneSpieleController {

	OffeneSpieleUI ui;
	RestopolyClient client;
	
	/**
	 * Initialisiert den Controller
	 * @param client Der RestClient, der mit den Services kommuniziert.
	 */
	public OffeneSpieleController(RestopolyClient client){
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
					ui = new OffeneSpieleUI();
					ladeOffeneSpiele();
					registriereActionListener();
					ui.showUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * L�dt die aktuellen offenen Spiele in die Tabelle
	 * @throws IOException 
	 * @throws UnirestException 
	 */
	private void ladeOffeneSpiele() throws IOException, UnirestException{
		GameInformationTableModel model = (GameInformationTableModel) ui.getOffeneSpieleTable().getModel();
		List<GameResponse> data = client.getGameResponses();
		model.loadData(data);
//		ui.getOffeneSpieleTable().updateUI();
	}
	
	/**
	 * Registriert die ActionListener an der UI,
	 * um Benutzereingaben zu verwerten.
	 */
	private void registriereActionListener() {
		
		JTable table = ui.getOffeneSpieleTable();
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				int row = table.getSelectedRow();
				if(row >= 0){
					GameInformationTableModel model = (GameInformationTableModel) table.getModel();

					GameResponse response = model.getGameResponse(row);
					ui.getLblAuswahl().setText(response.getName());
				}
			}
		});
		
		ui.getBtnBeitreten().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String gameID = ui.getLblAuswahl().getText();
				if(!gameID.equals("")){
					new GameController(client,gameID);
				}
			}
		});
		
	}
}
