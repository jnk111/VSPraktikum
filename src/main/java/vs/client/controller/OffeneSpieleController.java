package vs.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vs.client.model.GameInformation;
import vs.client.model.RestopolyClient;
import vs.client.model.tablemodel.GameInformationTableModel;
import vs.client.view.OffeneSpieleUI;

/**
 * Diese Klasse kontrolliert die Aktionen auf der OffeneSpieleUI,
 * verarbeitet die Aktivitäten des Users und kann ein neues Spiel starten.
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
		ui = new OffeneSpieleUI();
		ladeOffeneSpiele();
		registriereActionListener();
		ui.showUI();
	}
	
	/**
	 * Lädt die aktuellen offenen Spiele in die Tabelle
	 */
	private void ladeOffeneSpiele(){
		GameInformationTableModel model = (GameInformationTableModel) ui.getOffeneSpieleTable().getModel();
		List<GameInformation> data = client.getGameInformations();
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
					String id = (String) model.getValueAt(row, 0);
					ui.getLblAuswahl().setText(id);
				}
			}
		});
		
		ui.getBtnBeitreten().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String gameID = ui.getLblAuswahl().getText();
				if(!gameID.equals("")){
					GameController gameController = new GameController(client,gameID);
				}
			}
		});
		
	}
}
